package ro.licenta.pacienti.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Profile;
import ro.licenta.commons.domain.Subscription;
import ro.licenta.commons.repository.ProfileRepository;
import ro.licenta.commons.repository.SubscriptionRepository;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController extends DefaultController {

	@Autowired
	private SubscriptionRepository subscriptionRepository;
	@Autowired
	private ProfileRepository profileRepository;
	
	@GetMapping
	public Mono<List<Subscription>> findAllSubscriptions() {
		return subscriptionRepository.aggregateAll();
	}
	
	@PutMapping("/subscribe/{id}")
	public Mono<Profile> registerSubscription(@PathVariable("id") ObjectId id, @RequestParam("months") Long months) {
		return super.getCurrentUser()
			.flatMap(apiToken -> profileRepository.findByUser(apiToken.getUser()))
			.flatMap(profile -> {
				if (profile.getSubscription() != null && profile.isSubscriptionValid()) { // if we have a subscription, return
					return Mono.error(new IllegalArgumentException("User already has a subscription"));
				}
				return subscriptionRepository.aggregateById(id).flatMap(subscription -> {// find the subscription and link it to the user account
					profile.setSubscription(subscription);
					profile.setSubscriptionDate(LocalDateTime.now());
					profile.setSubscriptionExpirationDate(LocalDateTime.now().plusMonths(months));
					return profileRepository.save(profile);
				});
			});
	}
	
}
