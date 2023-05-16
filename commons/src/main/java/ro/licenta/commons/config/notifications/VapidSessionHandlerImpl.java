package ro.licenta.commons.config.notifications;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Subscription.Keys;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ro.licenta.commons.domain.ApiToken;

@Log4j2
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty("push.service.publicKey")
public class VapidSessionHandlerImpl implements VapidSessionHandler {

	@Autowired
	private PushService pushService;
	@Autowired
	private ObjectMapper objectMapper;
	private Subscription subscription;
	private VapidSession session;
	
	private final ApiToken apiToken;
	private Map<String, VapidSubscription> subscriptions = new ConcurrentHashMap<>();
	
	public VapidSessionHandlerImpl(ApiToken apiToken) {
		this.apiToken = apiToken;
	}
	
	@Override
	public String getId() {
		return apiToken.getKey();
	}

	@Override
	public ApiToken getPrincipal() {
		return apiToken;
	}

	@Override
	public Mono<VapidSessionHandler> connect(VapidSession session) {
		this.session = session;
		Keys keys = new Keys(session.getP256DH(), session.getAuth());
		this.subscription = new Subscription(session.getEndpoint(), keys);
		return Mono.just(this);
	}

	@Override
	public VapidSubscription send(Flux<Event> eventStream) {
		Disposable disposable = eventStream.flatMap(this::send).subscribeOn(Schedulers.boundedElastic()).subscribe();
		VapidSubscription subscription = new VapidSubscriptionImpl(apiToken.getKey(), disposable);
		subscriptions.put(apiToken.getKey(), subscription);
		return subscription;
	}

	@Override
	public Mono<Void> send(Event data) {
		try {
			Notification pushNotification = new Notification(subscription, objectMapper.writeValueAsString(data));
			return Mono.just(pushService.send(pushNotification))
				.onErrorResume(e -> Mono.empty())
				.doOnSuccess(e -> {
					log.trace("Sent event to vapid session: " + subscription.endpoint);
				}).then();
		} catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		return Mono.empty();
	}

	@Override
	public Mono<Void> send(Mono<Event> eventStream) {
		return eventStream.flatMap(this::send);
	}

	@Override
	public void cancelSubscription(String key) {
		VapidSubscription subscription = subscriptions.remove(key);
		if (subscription != null) {
			subscription.cancel();
		}
	}

	@Override
	public VapidSession getSession() {
		return this.session;
	}

	@Override
	public Mono<Void> close() {
		return Mono.fromRunnable(() -> {
			this.subscriptions.forEach((k,v) ->v.cancel());
			this.subscriptions.clear();
		});
	}

}
