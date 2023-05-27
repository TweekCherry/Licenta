package ro.licenta.commons.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import ro.licenta.commons.repository.ApiTokenRepository;

@Component
public class DefaultReactiveAuthenticationManager implements ReactiveAuthenticationManager {
	
	@Autowired
	private ApiTokenRepository apiTokenRepository;
	
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		return apiTokenRepository.findByKey(authentication.getPrincipal().toString())
			.filter(apiToken -> apiToken.isValid())
			.map(apiToken -> new UsernamePasswordAuthenticationToken(apiToken, authentication.getCredentials(), apiToken.getRoles()));
	}

}
