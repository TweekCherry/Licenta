package ro.licenta.commons.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Investigation;

public interface InvestigationRepositoryCustom {
	
	public Mono<UpdateResult> replaceType(String id, String name);
	
	public Mono<UpdateResult> replaceDepartment(String id, String name);
	
	public Mono<List<Investigation>> aggregateAll();
	
	public Mono<UpdateResult> unlinkClinic(ObjectId id);
	
	public Mono<UpdateResult> unlinkType(String id);
	
	public Mono<UpdateResult> unlinkDepartment(String id);
}
