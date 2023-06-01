/**
 * 
 */
package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;

@Repository
public interface ApiTokenRepository extends ReactiveMongoRepository<ApiToken, ObjectId> {
		
	public Mono<ApiToken> findByKey(String key);
	
	public Mono<Void> deleteByKey(String key);
	
	public Mono<Void> deleteAllByUser(ObjectId user);
	
}
