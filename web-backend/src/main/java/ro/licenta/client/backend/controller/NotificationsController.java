/**
 * 
 */
package ro.licenta.client.backend.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import ro.licenta.commons.config.notifications.DefaultEvent;
import ro.licenta.commons.config.notifications.VapidSession;
import ro.licenta.commons.config.notifications.VapidSessionHandler;
import ro.licenta.commons.config.notifications.VapidSessionHandlerImpl;
import ro.licenta.commons.config.notifications.VapidSessionImpl;
import ro.licenta.commons.config.notifications.VapidSessionRegistry;
import ro.licenta.commons.domain.VapidSubscription;
import ro.licenta.commons.repository.ApiTokenRepository;

/**
 * @author r.m.ghimis
 * @since Feb 7, 2022
 *
 */
@Log4j2
@RestController
@RequestMapping("/api/v2/notifications")
public class NotificationsController extends DefaultController {

	@Value("${push.service.publicKey}")
	private String publicKey;
	
	@Autowired
	private ObjectProvider<VapidSessionHandlerImpl> vapidSessionHandlerImplProvider;
	@Autowired
	protected VapidSessionRegistry vapidSessionHandlerRegistry;
//	@Autowired
//	private EventStreamService eventStreamService;
	@Autowired
	protected ApiTokenRepository apiTokenRepository;
	
//	@PostConstruct // TODO check for notifications
//	public void postConstruct() {
//		AsyncLoadingCache<Tuple2<String, String>, Boolean> permissionsCache = Caffeine.newBuilder()
//			.expireAfterAccess(Duration.ofMinutes(5))
//			.expireAfterWrite(Duration.ofMinutes(5))
//			.evictionListener((key, value, cause) -> log.info("Permissions key %s was evicted (%s)%n", key, cause))
//			.buildAsync((key, executor) -> permissionRepository.existsByClientIdAndHexacode(key.getT1(), key.getT2()).toFuture());
//		
//		eventStreamService.getDeviceStatusExchangeStream().filter(s -> s.getHexacode() != null).flatMap(e -> {
//			return Flux.concat(webSocketSessionRegistry.getAllSessions(), vapidSessionHandlerRegistry.getAllSessions()) //leave it like this
//				.filterWhen(s -> Mono.fromFuture(permissionsCache.get(Tuples.of(s.getPrincipal().getUser(), e.getHexacode()))))
//				.collectMultimap(s -> s.getPrincipal())
//				.flatMapMany(m -> Flux.fromIterable(m.entrySet()))
//				.flatMap(data -> {
//					Mono<Event> eventData = Mono.fromSupplier(() -> e).map(DefaultEvent::new);
//					if (e instanceof DeviceAlarmEntry event) {
//						eventData = alarmTranslator.translateAlarms(data.getKey().getUser(), data.getKey().getLanguageCode(), Arrays.asList(event))
//							.next()
//							.map(DefaultEvent::new);
//					}
//					return eventData.flatMapMany(d -> {
//						return Flux.fromIterable(data.getValue()).flatMap(s -> s.send(d));
//					});
//				});
//		}).subscribe();
//	}
	
	@GetMapping("/key")
	public Mono<String> getVapidKey() {
		return Mono.just(publicKey);
	}
	
    @PostMapping("/subscribe")
	@ResponseStatus(HttpStatus.NO_CONTENT)                                          
	public Mono<VapidSubscription> subscribe(@RequestBody VapidSubscription vapidSubscription) { 
		return super.getCurrentUser().flatMap(apiToken -> {
			 Mono<VapidSessionHandler> sessionHandler = vapidSessionHandlerRegistry.getSession(apiToken.getKey()); // look in cache
			 Mono<VapidSessionHandler> createHandler = Mono.defer(() -> {
				 Mono<Void> removeHandlersWithSameEndpoint = vapidSessionHandlerRegistry.getAllSessions()
					 .filter(h -> h.getSession().getEndpoint().equals(vapidSubscription.getEndpoint()))
					 .flatMap(h -> vapidSessionHandlerRegistry.removeSession(h.getId()))
					 .flatMap(h -> h.close())
					 .then();

            	 VapidSessionHandler vapidSessionHandler = vapidSessionHandlerImplProvider.getObject(apiToken);
				 VapidSession vapidSession  = new VapidSessionImpl()
					.setAuth(vapidSubscription.getKeys().getAuth())
					.setP256DH(vapidSubscription.getKeys().getP256dh())
					.setEndpoint(vapidSubscription.getEndpoint())
					.setId(apiToken.getKey());
				 apiToken.setVapidSubscription(vapidSubscription);
				 Mono<VapidSessionHandler> handlerRequest = apiTokenRepository.save(apiToken)
				 	.flatMap(t -> vapidSessionHandler.connect(vapidSession))
				 	.flatMap(vapidSessionHandlerRegistry::register);
				 
				 return removeHandlersWithSameEndpoint.then(handlerRequest);
			});
			return sessionHandler.switchIfEmpty(createHandler); 
		}).thenReturn(vapidSubscription);
	}
    
    @GetMapping("/unsubscribe")
    public Mono<Void> unsubscribePushNotification() { 
    	return super.getCurrentUser().flatMap(apiToken -> {    		
    		return vapidSessionHandlerRegistry.getSession(apiToken)
    			.doOnNext(h -> {
    				apiToken.setVapidSubscription(null);
    				h.cancelSubscription(apiToken.getKey());
    			})
    			.then(apiTokenRepository.save(apiToken))
    			.then();
    			
    	});
    }
    
    @GetMapping("/test-notification")
    public Mono<Void> sendAlarmTestNotification() {
    	return super.getCurrentUser().flatMapMany(apiToken -> {
    		return apiTokenRepository.findAll().filter(at -> at.getUser().equals(apiToken.getUser())) //here must not have more sessions with the same endpoint
    		.flatMap(token -> {
        		Mono<VapidSessionHandler> sessionHandler = vapidSessionHandlerRegistry.getSession(token.getKey()); // look in cache, filter by user
    			return sessionHandler;  			
    		}).flatMap(sh -> sh.send(new DefaultEvent<>("Here is your demo notification")));   	
    	}).then();
    }
}
