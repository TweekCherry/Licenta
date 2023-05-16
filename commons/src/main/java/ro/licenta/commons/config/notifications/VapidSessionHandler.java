package ro.licenta.commons.config.notifications;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VapidSessionHandler extends SessionHandler {
		
	/**
	 * Connect with the given session initializing this handler
	 * */
	public Mono<VapidSessionHandler> connect(VapidSession session);
	
	/**
	 * Register an event stream that will be used to send data to this websocket session
	 * */
	public VapidSubscription send(Flux<Event> eventStream);
		
	/**
	 * Return the current handler session details
	 * */
	public VapidSession getSession();
	
	/**
	 * Closes all current subscriptions
	 * */
	public Mono<Void> close();

}
