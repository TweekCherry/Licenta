/**
 * 
 */
package ro.licenta.commons.config.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ro.licenta.commons.amqp.AmqpEvent;
import ro.licenta.commons.amqp.Event;
import ro.licenta.commons.domain.ApiToken;

/**
 * This handles a websocket session registered by user, provides methods to send data to the user tab
 * */
@Log4j2
@Component
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketSessionHandler {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebSocketSessionRegistry webSocketSessionRegistry;
	
	@EqualsAndHashCode.Include
	private String id;
	@Getter
	private ApiToken apiToken;
	private WebSocketSession session;
	private Map<String, WebSocketSubscription> subscriptions = new ConcurrentHashMap<>();

	public WebSocketSessionHandler(ApiToken apiToken, WebSocketSession session) {
		this.apiToken = apiToken;
		this.session = session;
		this.id = session.getId();
	}

	public Mono<Void> connect() {
		Mono<Void> connect = webSocketSessionRegistry.register(this).doOnNext(h -> {
			log.info("[{}] Websocket session connected for user: {}", session.getId(), apiToken.getKey());
		}).then();
		Mono<Void> send = this.send(new AmqpEvent<SessionId>(new SessionId(session.getId())));
		Mono<Void> receive = session.receive()
			.map(WebSocketMessage::getPayloadAsText)
			.filter(m -> m.equals("heartbeat"))
			.map(session::textMessage)
			.map(Mono::just)
			.flatMap(session::send)
			.then();
		Mono<Void> diconnect = webSocketSessionRegistry.deregister(this).then(Mono.create(sink -> {
			log.info("[{}] Websocket session disconnected for user: ", session.getId(), apiToken.getKey());
			sink.success();
		}));
		return connect.then(send).then(receive).then(diconnect);
	}

	public Mono<Void> disconnect() {
		return Mono.create(sink -> {
			subscriptions.forEach((k, v) -> {
				log.info("[{}] Removing from session {} subscription: {}", id, k);
				v.cancel();
			});
			subscriptions.clear();
			sink.success();
		}).then(session.close());
	}

	public String getId() {
		return id;
	}

	public Mono<Void> send(Event data) {
		Mono<WebSocketMessage> pipiline = Mono.create(sink -> {
			try {
				String message = objectMapper.writeValueAsString(data);
				sink.success(message);
			} catch (Exception e) {
				sink.error(e);
			}
		}).map(String.class::cast).map(session::textMessage);
		return session.send(pipiline).onErrorResume(e -> {
			log.error("[{}] Erorr received while sending {} for user {}: {}", session.getId(), data, apiToken.getKey(), e.getMessage());
			return this.disconnect();
		});
	}

	public Mono<Void> send(Mono<Event> eventStream) {
		return eventStream.flatMap(this::send);
	}

	public WebSocketSubscription send(Flux<Event> eventStream) {
		Disposable disposable = eventStream.flatMap(this::send).subscribeOn(Schedulers.boundedElastic()).subscribe();
		WebSocketSubscription subscription = new WebSocketSubscription(disposable);
		subscriptions.put(subscription.getKey(), subscription);
		return subscription;
	}

	public void registerSubscription(WebSocketSubscription subscription) {
		subscriptions.put(subscription.getKey(), subscription);
	}

	public void cancelSubscription(String key) {
		WebSocketSubscription subscription = subscriptions.remove(key);
		if (subscription != null) {
			subscription.cancel();
		}
	}

	public Flux<WebSocketSubscription> getAllSubscriptions() {
		return Flux.create(sink -> {
			subscriptions.values().forEach(v -> {
				sink.next(v);
			});
			sink.complete();
		});
	}
}
