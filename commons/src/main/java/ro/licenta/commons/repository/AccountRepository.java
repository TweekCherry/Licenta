package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Account;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

	public Mono<Account> findByUsername(String username);
	
	public Mono<Account> findByEmail(String email);
	
	public Mono<Account> findByToken(String token);

	public Mono<Boolean> existsByUsername(String name);

	public Mono<Boolean> existsByUsernameAndIdNot(String name, String id);
	
	public Mono<Account> findById(String id);

}
