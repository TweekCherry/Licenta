package ro.licenta.commons.repository.custom;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.domain.Profile;

@Repository
public class MedicRepositoryCustomImpl implements MedicRepositoryCustom {

	@Autowired
	private ReactiveMongoTemplate mongodb;
	
	@Override
	public Flux<Medic> aggregateAll() {
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("deleted").is(false)); // exclude deleted medics
		AggregationOperation stage2 = TypedAggregation.lookup(Clinic.KEY_SPACE, "clinic", "_id", "clinicData"); // get the clinic data
		AggregationOperation stage3 = TypedAggregation.lookup(Profile.KEY_SPACE, "_id", "_id", "profileData"); //get the profile data
		AggregationOperation stage4 = TypedAggregation.lookup(Account.KEY_SPACE, "_id", "_id", "accountData"); // get the account data
		AggregationOperation stage5 = TypedAggregation.addFields().addFieldWithValue("clinicData", ArrayOperators.First.first("$clinicData")).build(); // transform clinic data from array to object
		AggregationOperation stage6 = TypedAggregation.addFields().addFieldWithValue("profileData", ArrayOperators.First.first("$profileData")).build(); // transform profile data from array to object
		AggregationOperation stage7 = TypedAggregation.addFields().addFieldWithValue("accountData", ArrayOperators.First.first("$accountData")).build(); // transform account data from array to object
		
		TypedAggregation<Medic> aggregation = TypedAggregation.newAggregation(Medic.class, 
			stage1, stage2, stage3, stage4, stage5, stage6, stage7
		).withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Medic.KEY_SPACE, Medic.class);
	}

	@Override
	public Flux<Medic> aggregateAllByClinic(ObjectId clinic) {
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("deleted").is(false).and("clinic").is(clinic)); // exclude deleted medics and match clinic
		AggregationOperation stage2 = TypedAggregation.lookup(Clinic.KEY_SPACE, "clinic", "_id", "clinicData"); // get the clinic data
		AggregationOperation stage3 = TypedAggregation.lookup(Profile.KEY_SPACE, "_id", "_id", "profileData"); //get the profile data
		AggregationOperation stage4 = TypedAggregation.lookup(Account.KEY_SPACE, "_id", "_id", "accountData"); // get the account data
		AggregationOperation stage5 = TypedAggregation.addFields().addFieldWithValue("clinicData", ArrayOperators.First.first("$clinicData")).build(); // transform clinic data from array to object
		AggregationOperation stage6 = TypedAggregation.addFields().addFieldWithValue("profileData", ArrayOperators.First.first("$profileData")).build(); // transform profile data from array to object
		AggregationOperation stage7 = TypedAggregation.addFields().addFieldWithValue("accountData", ArrayOperators.First.first("$accountData")).build(); // transform account data from array to object
		
		TypedAggregation<Medic> aggregation = TypedAggregation.newAggregation(Medic.class, 
			stage1, stage2, stage3, stage4, stage5, stage6, stage7
		).withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Medic.KEY_SPACE, Medic.class);
	}

	@Override
	public Mono<UpdateResult> unlinkClinic(ObjectId id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("clinic").is(id));
		
		Update update = new Update();
		update.unset("clinic");
		return mongodb.updateMulti(query, update, Medic.class);
	}

	@Override
	public Mono<UpdateResult> unlinkDepartment(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("departments").is(id));
		
		Update update = new Update();
		update.pull("departments", id);
		
		return mongodb.updateMulti(query, update, Medic.class);
	}

	@Override
	public Mono<UpdateResult> replaceDepartment(String id, String name) {
		Query query = new Query();
		query.addCriteria(Criteria.where("departments").is(id));
		
		Update update = new Update();
		update.pull("departments", id);
		update.addToSet("departments", name);
		
		return mongodb.updateMulti(query, update, Medic.class);
	}

}
