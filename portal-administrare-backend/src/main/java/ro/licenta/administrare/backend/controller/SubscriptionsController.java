package ro.licenta.administrare.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
		return subscriptionRepository.aggregateAll();
	}
	
	@PostMapping
	public Mono<Subscription> save(@RequestBody Subscription subscription) {
		return subscriptionRepository.save(subscription);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") ObjectId id) {
		return subscriptionRepository.deleteById(id);
	}
}
