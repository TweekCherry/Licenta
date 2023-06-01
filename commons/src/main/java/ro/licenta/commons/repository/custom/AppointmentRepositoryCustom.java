package ro.licenta.commons.repository.custom;

import org.bson.types.ObjectId;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;

public interface AppointmentRepositoryCustom {

	public Mono<UpdateResult> cancelByMedic(ObjectId id);

}
