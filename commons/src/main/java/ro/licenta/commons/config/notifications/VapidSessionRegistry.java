package ro.licenta.commons.config.notifications;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;

public interface VapidSessionRegistry {
	
	public Flux<VapidSessionHandler> getAllSessions();
	
	public Flux<VapidSessionHandler> sessionsStream();
	
	public Mono<VapidSessionHandler> register(VapidSessionHandler webSocketSessionHandler);
	
	public Mono<VapidSessionHandler> getSession(String sessionId);
	
	public Mono<VapidSessionHandler> getSession(ApiToken apiToken);
	
	public Mono<VapidSessionHandler> removeSession(ApiToken apiToken);
	
	public Mono<VapidSessionHandler> removeSession(String sessionId);
	
}
