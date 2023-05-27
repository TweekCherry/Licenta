package ro.licenta.commons.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.ApiToken;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
	
	public Mono<Account> findByEmail(String email);
	
	public Mono<Account> findByToken(String token);
	
	public Mono<Boolean> existsByToken(String token);

	public Mono<Boolean> existsByEmail(String email);
	
}
