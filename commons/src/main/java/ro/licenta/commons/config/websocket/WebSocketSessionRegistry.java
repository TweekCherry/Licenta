/**
 * 
 */
package ro.licenta.commons.config.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import ro.licenta.commons.domain.ApiToken;

/**
 * Here we keep all websocket sessions per user account
 * */
@Log4j2
@Component
public class WebSocketSessionRegistry {
	
	private Many<WebSocketSessionHandler> sessionsSink = Sinks.many().multicast().directBestEffort();
	private ConcurrentMap<String, ConcurrentMap<String, WebSocketSessionHandler>> cache = new ConcurrentHashMap<>();
	
	public Mono<WebSocketSessionHandler> register(WebSocketSessionHandler webSocketSessionHandler) {
		return Mono.create(sink -> {
			ConcurrentMap<String, WebSocketSessionHandler> handlers = cache.get(webSocketSessionHandler.getApiToken().getKey());
			if (handlers == null) {
				handlers = new ConcurrentHashMap<>();
				cache.put(webSocketSessionHandler.getApiToken().getKey(), handlers);
			}
			log.info("Registering websocket session: " + webSocketSessionHandler.getId() + " for user: " + webSocketSessionHandler.getApiToken().getKey());
			handlers.put(webSocketSessionHandler.getId(), webSocketSessionHandler);
			sink.success(webSocketSessionHandler);
			sessionsSink.tryEmitNext(webSocketSessionHandler);
		});
	}

	public Mono<WebSocketSessionHandler> deregister(WebSocketSessionHandler webSocketSessionHandler) {
		Mono<WebSocketSessionHandler> results = Mono.create(sink -> {
			ApiToken apiToken = webSocketSessionHandler.getApiToken();
			cache.remove(apiToken.getKey());
			log.info("Removing websocket sessions from registry for user: " + apiToken.getKey());
			sink.success(webSocketSessionHandler);
		});
		return webSocketSessionHandler.disconnect().then(results);
	}

	public Mono<WebSocketSessionHandler> getSession(String sessionId) {
		return Flux.fromStream(cache.values().stream().flatMap(e -> e.values().stream()))
			.filter(h -> h.getId().equals(sessionId))
			.next();
	}

	public Flux<WebSocketSessionHandler> getSessions(ApiToken apiToken) {
		return Mono.justOrEmpty(cache.get(apiToken.getKey()))
			.flatMapMany(e -> Flux.fromIterable(e.values()));
	}

	public Mono<Void> removeSession(ApiToken apiToken) {
		log.info("Removing websocket sessions from registry for user: " + apiToken.getKey());
		return Mono.justOrEmpty(cache.remove(apiToken.getKey()))
			.map(ConcurrentMap::values)
			.flatMapMany(Flux::fromIterable)
			.flatMap(h -> h.disconnect())
			.then();
		
	}

	public Flux<WebSocketSessionHandler> getAllSessions() {
		return Flux.create(sink -> {
			cache.values().stream().flatMap(v -> v.values().stream()).forEach(v -> {
				sink.next(v);
			});
			sink.complete();
		});
	}

	public Mono<Void> removeSessionByToken(String token) {
		log.info("Removing websocket session from registry for user: " + token);
		return Mono.create(sink -> {
			ConcurrentMap<String, WebSocketSessionHandler> sessions = cache.remove(token);
			if (sessions != null) {
				sessions.values().forEach(h -> h.disconnect());
			}
			sink.success();
		});
	}
	
	public Flux<WebSocketSessionHandler> sessionsStream() {
		return sessionsSink.asFlux();
	}
}
