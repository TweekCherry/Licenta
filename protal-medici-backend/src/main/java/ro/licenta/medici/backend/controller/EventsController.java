package ro.licenta.medici.backend.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import ro.licenta.commons.amqp.AmqpEvent;
import ro.licenta.commons.amqp.AppointmentCancelled;
import ro.licenta.commons.amqp.AppointmentCreated;
import ro.licenta.commons.amqp.Event;
import ro.licenta.commons.config.websocket.WebSocketSessionRegistry;
import ro.licenta.medici.backend.components.AmqpListener;

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
			Flux<Event> appointmentCreatedEventStream = amqpListener.eventStream()
				.filter(AppointmentCreated.class::isInstance)
				.map(AppointmentCreated.class::cast)
				.filter(a -> a.getAppointment().getMedic().equals(session.getApiToken().getUser()))
				.map(AmqpEvent::new);
			Flux<Event> appointmentCancelledEventStream = amqpListener.eventStream()
					.filter(AppointmentCancelled.class::isInstance)
					.map(AppointmentCancelled.class::cast)
					.filter(a -> a.getAppointment().getMedic().equals(session.getApiToken().getUser()))
					.map(AmqpEvent::new);
			session.send(appointmentCreatedEventStream);
			session.send(appointmentCancelledEventStream);
			return session;
		}).subscribe();
	}
	
}
