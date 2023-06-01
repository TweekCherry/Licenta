package ro.licenta.commons.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.domain.Investigation;

@Repository
public class InvestigationRepositoryCustomImpl implements InvestigationRepositoryCustom {
	
	@Autowired
	private ReactiveMongoTemplate mongodb;
	
	@Override
	public Mono<UpdateResult> replaceType(String id, String name) {
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(id));
		
		Update update = new Update();
		update.set("type", name);
		
		return mongodb.updateMulti(query, update, Investigation.class);
	}

	@Override
	public Mono<UpdateResult> replaceDepartment(String id, String name) {
		Query query = new Query();
		query.addCriteria(Criteria.where("department").is(id));
		
		Update update = new Update();
		update.set("department", name);
		
		return mongodb.updateMulti(query, update, Investigation.class);
	}

	@Override
	public Mono<UpdateResult> unlinkClinic(ObjectId id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("clinics").is(id));
		
		Update update = new Update();
		update.pull("clinics", id);
		
		return mongodb.updateMulti(query, update, Investigation.class);
	}

	@Override
	public Mono<UpdateResult> unlinkDepartment(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("department").is(id));
		
		Update update = new Update();
		update.unset("department");
		
		return mongodb.updateMulti(query, update, Investigation.class);
	}

	@Override
	public Mono<UpdateResult> unlinkType(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(id));
		
		Update update = new Update();
		update.unset("type");
		
		return mongodb.updateMulti(query, update, Investigation.class);
	}

	@Override
	public Mono<List<Investigation>> aggregateAll() {
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("id").exists(true));
		AggregationOperation stage2 = TypedAggregation.lookup(Clinic.KEY_SPACE, "clinics", "_id", "clinicsData");
		
		TypedAggregation<Investigation> aggregation = TypedAggregation.newAggregation(Investigation.class, stage1, stage2)
                .withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Investigation.KEY_SPACE, Investigation.class).collectList();
	}

}
