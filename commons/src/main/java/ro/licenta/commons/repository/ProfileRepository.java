package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Profile;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, ObjectId> {
	
	public Mono<Profile> findByUser(ObjectId user);
	
}
