package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Profile;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
	
	public Mono<Profile> findByUser(String user);
	
}
