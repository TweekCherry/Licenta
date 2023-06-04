package ro.licenta.pacienti.backend.components;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import ro.licenta.commons.components.AmqpDeclarations;

@Component
public class AmqpListener {
	
	@Autowired
	private AmqpTemplate ampqTemplate;
	@Autowired
	private ObjectMapper objectMapper;
	private MessageConverter messageConverter;
	
	private Many<Object> inbound = Sinks.many().multicast().directAllOrNothing();
	private Many<Object> outbound = Sinks.many().multicast().directAllOrNothing();
	
	@PostConstruct
	public void postConstruct() {
		messageConverter = new Jackson2JsonMessageConverter(objectMapper);
		outbound.asFlux().subscribe(event -> {
			 Message message = messageConverter.toMessage(event, null);
			 ampqTemplate.send(AmqpDeclarations.MEDICS_EXCHANGE, "", message);
		});
	}
	
	@RabbitListener(bindings = 
		@QueueBinding(
	        value = @Queue(value = "", autoDelete = "true"),
	        exchange = @Exchange(value = AmqpDeclarations.PATIENTS_EXCHANGE)
		)
	)
	private void amqpListener(Message message) {
		Object event = messageConverter.fromMessage(message); 
		inbound.tryEmitNext(event);
	}
	
	public Flux<Object> eventStream() {
		return inbound.asFlux();
	}
	
	/**
	 * Send a notification to the medics backend via rabbitMQ server
	 * */
	public <T> void notifyMedics(T object) {
		outbound.tryEmitNext(object);
	}
}
