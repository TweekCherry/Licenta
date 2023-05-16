package ro.licenta.commons.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import ro.licenta.commons.service.ReactiveAutenticationService;

@Component
public class DefaultReactiveAuthenticationManager implements ReactiveAuthenticationManager {
	
	@Autowired
	private ReactiveAutenticationService reactiveAutenticationService;
	
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		return reactiveAutenticationService.verifyToken(authentication.getPrincipal().toString())
			.map(apiToken -> new UsernamePasswordAuthenticationToken(apiToken, authentication.getCredentials(), apiToken.getAuthorities()));
	}

}
