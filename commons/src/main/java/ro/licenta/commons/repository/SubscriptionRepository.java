package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import ro.licenta.commons.domain.Subscription;
import ro.licenta.commons.repository.custom.SubscriptionRepositoryCustom;

@Repository
public interface SubscriptionRepository extends ReactiveMongoRepository<Subscription, ObjectId>, SubscriptionRepositoryCustom {
		
}
