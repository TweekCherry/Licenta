package ro.licenta.commons.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Subscription;

public interface SubscriptionRepositoryCustom {

	public Mono<List<Subscription>> aggregateAll();

	public Mono<Subscription> aggregateById(ObjectId id);
	
	public Mono<UpdateResult> unlinkInvestigation(ObjectId id);

}
