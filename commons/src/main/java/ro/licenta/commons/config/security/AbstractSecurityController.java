package ro.licenta.commons.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.BasicAuthenticationRequest;
import ro.licenta.commons.service.ReactiveAutenticationService;

@RestController
public class AbstractSecurityController {
	
	@Autowired
	private ReactiveAutenticationService reactiveAutenticationService;
	
	@PostMapping("/login-basic")
	public Mono<ResponseEntity<Object>> authenticateBasic(
			@RequestBody BasicAuthenticationRequest autenticationRequest,
			ServerHttpRequest request) {
		Mono<ResponseEntity<Object>> response = handleBasicAuthenticationRequest(autenticationRequest);
		return response.onErrorResume(BadCredentialsException.class, e -> {
			return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
		}).onErrorResume(InsufficientAuthenticationException.class, e -> {
			return Mono.just(new ResponseEntity<>(HttpStatus.ACCEPTED));
		}).onErrorResume(LockedException.class, e -> {
			return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.LOCKED));
		});
	}
	
	private Mono<ResponseEntity<Object>> handleBasicAuthenticationRequest(BasicAuthenticationRequest autenticationRequest) {
		return reactiveAutenticationService.authenticate(autenticationRequest)
				.map(a -> new ResponseEntity<>(a, HttpStatus.OK));
	}
}
