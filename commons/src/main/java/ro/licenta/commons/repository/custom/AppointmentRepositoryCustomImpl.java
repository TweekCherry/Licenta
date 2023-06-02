package ro.licenta.commons.repository.custom;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.aggregation.UnsetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.domain.Appointment.AppoimentStatus;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.domain.Investigation;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.domain.Profile;

@Repository
public class AppointmentRepositoryCustomImpl implements AppointmentRepositoryCustom {

	@Autowired
	private ReactiveMongoTemplate mongodb;
	
	@Override
	public Mono<UpdateResult> cancelByMedic(ObjectId id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("medic").is(id));
		
		Update update = new Update();
		update.set("status", AppoimentStatus.CANCELLED);
		
		return mongodb.updateMulti(query, update, Appointment.KEY_SPACE);
	}
	
	@Override
	public Flux<Appointment> aggregateAll(ObjectId medic) {
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("medic").is(medic));
		AggregationOperation stage2 = TypedAggregation.lookup(Profile.KEY_SPACE, "user", "_id", "userData"); //get the user profile data
		AggregationOperation stage3 = TypedAggregation.lookup(Clinic.KEY_SPACE, "clinic", "_id", "clinicData"); // get the clinic data
		AggregationOperation stage4 = TypedAggregation.lookup(Medic.KEY_SPACE, "medic", "_id", "medicData"); // get the medic data
		AggregationOperation stage5 = TypedAggregation.lookup(Investigation.KEY_SPACE, "investigation", "_id", "investigationData"); // get the investigation data
		AggregationOperation stage6 = TypedAggregation.lookup(Profile.KEY_SPACE, "medicData.id", "_id", "medicData.profileData"); // get the medic profile data
		
		AggregationOperation stage7 = TypedAggregation.addFields().addFieldWithValue("userData", ArrayOperators.First.first("$userData")).build(); // transform clinic data from array to object
		AggregationOperation stage8 = TypedAggregation.addFields().addFieldWithValue("clinicData", ArrayOperators.First.first("$clinicData")).build(); // transform profile data from array to object
		AggregationOperation stage9 = TypedAggregation.addFields().addFieldWithValue("medicData", ArrayOperators.First.first("$medicData")).build(); // transform account data from array to object
		AggregationOperation stage10 = TypedAggregation.addFields().addFieldWithValue("investigationData", ArrayOperators.First.first("$investigationData")).build(); // transform account data from array to object
		AggregationOperation stage11 = TypedAggregation.addFields().addFieldWithValue("medicData.profileData", ArrayOperators.First.first("$medicData.profileData")).build(); // transform account data from array to object
		
		TypedAggregation<Appointment> aggregation = TypedAggregation.newAggregation(Appointment.class, 
			stage1, stage2, stage3, stage4, stage5, stage6, stage7, stage8, stage9, stage10, stage11
		).withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Appointment.KEY_SPACE, Appointment.class);
	}

	@Override
	public Mono<List<LocalDateTime>> findBookedAppointmentDates(ObjectId clinic, ObjectId medic) {
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("medic").is(medic));
		AggregationOperation stage2 = TypedAggregation.match(Criteria.where("clinic").is(clinic));
		AggregationOperation stage3 = TypedAggregation.match(Criteria.where("status").is(AppoimentStatus.SCHEDULED));
		AggregationOperation stage4 = TypedAggregation.project().andInclude("timestamp");
		
		TypedAggregation<Appointment> aggregation = TypedAggregation.newAggregation(Appointment.class, 
			stage1, stage2, stage3, stage4
		).withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Appointment.KEY_SPACE, Document.class)
			.map(document -> document.get("timestamp", Date.class))
			.map(d -> d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
			.collectList();
	}

	@Override
	public Flux<Appointment> aggregateByUser(ObjectId user) {
		AggregationOperation stage1 = TypedAggregation.match(Criteria.where("user").is(user));
		AggregationOperation stage2 = TypedAggregation.lookup(Profile.KEY_SPACE, "user", "_id", "userData"); //get the user profile data
		AggregationOperation stage3 = TypedAggregation.lookup(Clinic.KEY_SPACE, "clinic", "_id", "clinicData"); // get the clinic data
		AggregationOperation stage4 = TypedAggregation.lookup(Medic.KEY_SPACE, "medic", "_id", "medicData"); // get the medic data
		AggregationOperation stage5 = TypedAggregation.lookup(Investigation.KEY_SPACE, "investigation", "_id", "investigationData"); // get the investigation data

		AggregationOperation stage6 = TypedAggregation.addFields().addFieldWithValue("userData", ArrayOperators.First.first("$userData")).build(); // transform clinic data from array to object
		AggregationOperation stage7 = TypedAggregation.addFields().addFieldWithValue("clinicData", ArrayOperators.First.first("$clinicData")).build(); // transform profile data from array to object
		AggregationOperation stage8 = TypedAggregation.addFields().addFieldWithValue("medicData", ArrayOperators.First.first("$medicData")).build(); // transform account data from array to object
		AggregationOperation stage9 = TypedAggregation.addFields().addFieldWithValue("investigationData", ArrayOperators.First.first("$investigationData")).build(); // transform account data from array to object

		AggregationOperation stage10 = TypedAggregation.lookup(Profile.KEY_SPACE, "medicData._id", "_id", "medicProfile"); // get the medic profile data
		AggregationOperation stage11 = TypedAggregation.addFields().addFieldWithValue("medicData.profileData", ArrayOperators.First.first("$medicProfile")).build(); // transform account data from array to object
		AggregationOperation stage12 = UnsetOperation.unset("medicProfile"); // remove the array of medic profile
		
		
		
		TypedAggregation<Appointment> aggregation = TypedAggregation.newAggregation(Appointment.class, 
			stage1, stage2, stage3, stage4, stage5, stage6, stage7, stage8, stage9, stage10, stage11, stage12
		).withOptions(TypedAggregation.newAggregationOptions().allowDiskUse(true).build());
		
		return mongodb.aggregate(aggregation, Appointment.KEY_SPACE, Appointment.class);
	}

}
