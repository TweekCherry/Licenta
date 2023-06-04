package ro.licenta.pacienti.backend.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import ro.licenta.commons.amqp.AmqpEvent;
import ro.licenta.commons.amqp.AppointmentCancelled;
import ro.licenta.commons.amqp.AppointmentFinished;
import ro.licenta.commons.amqp.AppointmentStarted;
import ro.licenta.commons.amqp.Event;
import ro.licenta.commons.config.websocket.WebSocketSessionRegistry;
import ro.licenta.pacienti.backend.components.AmqpListener;

@Component
public class EventsController {
	
	@Autowired
	private AmqpListener amqpListener;
	
	@Autowired
	private WebSocketSessionRegistry webSocketSessionRegistry;
	
	@PostConstruct
	public void postConstruct() {
		webSocketSessionRegistry.sessionsStream().map(session -> {
			// when a session it's registered, add some events to it
			Flux<Event> appointmentCancelledEventStream = amqpListener.eventStream()
				.filter(AppointmentCancelled.class::isInstance)
				.map(AppointmentCancelled.class::cast)
				.filter(a -> a.getAppointment().getUser().equals(session.getApiToken().getUser())) // make sure that we send to the right user
				.map(AmqpEvent::new);
			Flux<Event> appointmentStartedEventStream = amqpListener.eventStream()
					.filter(AppointmentStarted.class::isInstance)
					.map(AppointmentStarted.class::cast)
					.filter(a -> a.getAppointment().getUser().equals(session.getApiToken().getUser())) // make sure that we send to the right user
					.map(AmqpEvent::new);
			Flux<Event> appointmentFinishedEventStream = amqpListener.eventStream()
					.filter(AppointmentFinished.class::isInstance)
					.map(AppointmentFinished.class::cast)
					.filter(a -> a.getAppointment().getUser().equals(session.getApiToken().getUser())) // make sure that we send to the right user
					.map(AmqpEvent::new);
			session.send(appointmentFinishedEventStream);
			session.send(appointmentStartedEventStream);
			session.send(appointmentCancelledEventStream);
			return session;
		}).subscribe();
	}
	
}
