package ro.licenta.commons.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.components.ReactiveMap;
import ro.licenta.commons.domain.Investigation;
import ro.licenta.commons.domain.Subscription;

@Repository
public class SubscriptionRepositoryCustomImpl implements SubscriptionRepositoryCustom {
	
	@Autowired
	private ReactiveMongoTemplate mongodb;

	@Override
	public Mono<List<Subscription>> aggregateAll() {
		
		return mongodb.findAll(Subscription.class)// find all subscriptions
			.flatMap(this::linkBenefits)
			.collectList(); // collect everything in a list
	}

	@Override
	public Mono<Subscription> aggregateById(ObjectId id) {
		return mongodb.findById(id, Subscription.class)
			.flatMap(this::linkBenefits);
	}
	
	@Override
	public Mono<UpdateResult> unlinkInvestigation(ObjectId id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("benefits.investigation").is(id));
		
		Update update = new Update();
		update.pull("benefits", new Query(Criteria.where("investigation").is(id)));
		
		return mongodb.updateMulti(query, update, Subscription.class);
	}
	
	
	private Mono<Subscription> linkBenefits(Subscription subscription) {
		ReactiveMap<ObjectId, Investigation> investigations = new ReactiveMap<>(key -> mongodb.findById(key, Investigation.class));
		return Flux.fromIterable(subscription.getBenefits()) // for each benefit
			.flatMap(benefit -> investigations.find(benefit.getInvestigation())// find the investigation and cache it
			.map(i -> benefit.setInvestigationData(i))) // set the investigation data on the benefit
			.then(Mono.just(subscription)); // then return the initial subscription
	}
}
