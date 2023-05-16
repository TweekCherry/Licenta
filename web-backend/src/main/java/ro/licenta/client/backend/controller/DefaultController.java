/**
 * 
 */
package ro.licenta.client.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;

/**
 * @author r.m.ghimis
 * @since Nov 29, 2018
 */
public abstract class DefaultController {
	 
	protected Mono<ApiToken> getCurrentUser() {
		return ReactiveSecurityContextHolder.getContext()
			.flatMap(c -> Mono.justOrEmpty(c.getAuthentication()))
			.flatMap(c -> Mono.justOrEmpty(c.getPrincipal()))
			.map(ApiToken.class::cast);
	}
	
	protected Mono<ApiToken> hasRole(String role) {
		return getCurrentUser()
			.filter(t -> t.hasRole(role));
	}
	
	public <T> Mono<T> responseStatus(ServerHttpResponse response, HttpStatus status) {
		return Mono.create(sink -> {
			response.setStatusCode(status);
			sink.success();
		});
	}
}
