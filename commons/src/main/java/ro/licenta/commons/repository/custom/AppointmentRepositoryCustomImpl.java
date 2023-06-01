package ro.licenta.commons.repository.custom;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.domain.Appointment.AppoimentStatus;

@Repository
public class AppointmentRepositoryCustomImpl implements AppointmentRepositoryCustom {

	@Autowired
	private ReactiveMongoTemplate monogdb;
	
	@Override
	public Mono<UpdateResult> cancelByMedic(ObjectId id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("medic").is(id));
		
		Update update = new Update();
		update.set("status", AppoimentStatus.CANCELLED);
		
		return monogdb.updateMulti(query, update, Appointment.KEY_SPACE);
	}

}
