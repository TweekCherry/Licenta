package ro.licenta.commons.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.domain.Investigation;

@Repository
public class ClinicRepositoryCustomImpl implements ClinicRepositoryCustom {

	@Autowired
	private ReactiveMongoTemplate mongodb;
	
	@Override
	public Mono<List<Clinic>> findByInvestigationId(ObjectId id) {
		
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("_id").is(id));
		AggregationOperation stage2 = TypedAggregation.unwind("clinics");
		AggregationOperation stage3 = TypedAggregation.lookup(Clinic.KEY_SPACE, "clinics", "_id", "clinicData");
		AggregationOperation stage4 = TypedAggregation.unwind("clinicData");
		AggregationOperation stage5 = TypedAggregation.replaceRoot("clinicData");

		TypedAggregation<Investigation> aggregation = TypedAggregation.newAggregation(Investigation.class, 
			stage1, stage2, stage3, stage4, stage5
		).withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Investigation.KEY_SPACE, Clinic.class).collectList();
	}

}
