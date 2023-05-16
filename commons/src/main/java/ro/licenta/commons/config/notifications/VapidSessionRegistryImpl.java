package ro.licenta.commons.config.notifications;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.repository.ApiTokenRepository;
import ro.licenta.commons.domain.VapidSubscription;

@Log4j2
@Component
@ConditionalOnProperty("push.service.publicKey")
public class VapidSessionRegistryImpl implements VapidSessionRegistry {

	private Many<VapidSessionHandler> sessionsSink = Sinks.many().multicast().directBestEffort();
	private ConcurrentMap<String, VapidSessionHandler> cache = new ConcurrentHashMap<>();
	@Autowired
	private ApiTokenRepository apiTokenRepository;
	@Autowired
	private ObjectProvider<VapidSessionHandlerImpl> vapidSessionHandlerImplProvider;
	
    @PostConstruct
    public void postConstruct() {
    	log.info("Loading vapid sessions");
		apiTokenRepository.findAll()
		.filter(ApiToken.class::isInstance)
		.map(ApiToken.class::cast)
		.filter(t -> t.getVapidSubscription() != null)
		.flatMap(apiToken -> {
			cache.entrySet().stream()
				.filter(e -> e.getValue().getSession().getEndpoint().equals(apiToken.getVapidSubscription().getEndpoint()))
				.filter(e -> e.getValue().getPrincipal().getCreatedAt().isBefore(apiToken.getCreatedAt()))
				.forEach(e -> {
					log.info("Vapid session duplicate, removing old session: {}", e.getValue().getPrincipal().getId());
					cache.remove(e.getKey()).close().subscribe();
				});
			VapidSubscription subscription = apiToken.getVapidSubscription();
			VapidSessionHandler sessionHandler = vapidSessionHandlerImplProvider.getObject(apiToken);
	        VapidSession vapidSession  = new VapidSessionImpl()
					.setAuth(subscription.getKeys().getAuth())
					.setP256DH(subscription.getKeys().getP256dh())
					.setEndpoint(subscription.getEndpoint())
					.setId(apiToken.getKey());
	        return sessionHandler.connect(vapidSession).flatMap(this::register);	        
		}).subscribe();
    }

	@Override
	public Mono<VapidSessionHandler> register(VapidSessionHandler vapidSessionHandler) {
		return Mono.defer(() -> {
			log.info("Registering vapid session: " + vapidSessionHandler.getId());
			cache.put(vapidSessionHandler.getId(), vapidSessionHandler);
			return Mono.just(vapidSessionHandler);
		}).doOnNext(s -> sessionsSink.tryEmitNext(s));
	}

	@Override
	public Mono<VapidSessionHandler> getSession(String sessionId) {
		return Mono.justOrEmpty(cache.get(sessionId));
	}

	@Override
	public Mono<VapidSessionHandler> getSession(ApiToken apiToken) {
		return Mono.justOrEmpty(cache.get(apiToken.getKey()));
	}

	@Override
	public Mono<VapidSessionHandler> removeSession(ApiToken apiToken) {
		log.info("Removing vapid session from registry: " + apiToken.getKey());
		return Mono.justOrEmpty(cache.remove(apiToken.getKey()));
	}

	@Override
	public Mono<VapidSessionHandler> removeSession(String sessionId) {
		log.info("Removing vapid session from registry: " + sessionId);
		return Mono.justOrEmpty(cache.remove(sessionId));
	}

	@Override
	public Flux<VapidSessionHandler> getAllSessions() {
		return Flux.fromIterable(cache.values());
	}
	
	@Override
	public Flux<VapidSessionHandler> sessionsStream() {
		return sessionsSink.asFlux();
	}

}
