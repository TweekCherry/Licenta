package ro.licenta.commons.config.notifications;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;

public interface SessionHandler {
	
	public String getId();
	
	public ApiToken getPrincipal();
	
	/**
	 * Cancel an event stream subscription based on the given key
	 * */
	public void cancelSubscription(String key);
	
	/**
	 * Send some data to this session
	 * */
	public Mono<Void> send(Event data);
	
	/**
	 * Send some data to this session
	 * */
	public Mono<Void> send(Mono<Event> eventStream);
}
