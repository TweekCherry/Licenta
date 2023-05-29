package ro.licenta.pacienti.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Subscription;
import ro.licenta.commons.repository.SubscriptionRepository;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController extends DefaultController {

	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@GetMapping
	public Mono<List<Subscription>> findAllSubscriptions() {
		return subscriptionRepository.findAll().collectList();
	}
	
}
