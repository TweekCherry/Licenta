package ro.licenta.commons.repository.custom;

import org.bson.types.ObjectId;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Medic;

public interface MedicRepositoryCustom {

	public Flux<Medic> aggregateAll();
	
	public Flux<Medic> aggregateAllByClinic(ObjectId clinic);

	public Mono<UpdateResult> unlinkClinic(ObjectId id);
	
	public Mono<UpdateResult> unlinkDepartment(String id);
	
	public Mono<UpdateResult> replaceDepartment(String id, String name);
}
