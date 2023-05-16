package ro.licenta.commons.service;

import reactor.core.publisher.Mono;
import ro.licenta.commons.BasicAuthenticationRequest;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.ApiToken;

public interface ReactiveAutenticationService {
	
	public Mono<Void> removeUserTokens(String userId);
	
	public Mono<Account> verifyCredentials(String username, String password);
	
	public Mono<Void> clearApiToken(String key);
	
	public Mono<ApiToken> verifyToken(String key);
	
	public Mono<ApiToken> authenticate(BasicAuthenticationRequest authenticationRequest);

}
