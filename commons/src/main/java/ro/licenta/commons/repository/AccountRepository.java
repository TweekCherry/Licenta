package ro.licenta.commons.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Account;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, ObjectId> {
	
	public Mono<Account> findByEmail(String email);
	
	public Mono<Account> findByToken(String token);

	public Mono<Boolean> existsByEmail(String email);
	
	public Mono<Boolean> existsByEmailAndIdNot(String email, ObjectId id);
	
	public Mono<Long> countByRoles(String role);
	
}
