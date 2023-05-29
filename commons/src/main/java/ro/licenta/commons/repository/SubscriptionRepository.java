package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Subscription;

@Repository
public interface SubscriptionRepository extends ReactiveMongoRepository<Subscription, String> {
		
}
