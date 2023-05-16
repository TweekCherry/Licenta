package ro.licenta.commons.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class DefaultReactiveServerSecurityContextRepository implements ServerSecurityContextRepository {

	@Autowired
	private ReactiveAuthenticationManager reactiveAuthenticationManager;
	
	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return Mono.empty();
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
			.flatMap(this::extractJWTToken)
			.switchIfEmpty(Mono.justOrEmpty(exchange.getRequest().getQueryParams().getFirst(HttpHeaders.AUTHORIZATION)))
			.map(token -> new UsernamePasswordAuthenticationToken(token, exchange.getRequest().getRemoteAddress().toString()))
			.flatMap(reactiveAuthenticationManager::authenticate)
			.map(SecurityContextImpl::new);
	}
	
	private Mono<String> extractJWTToken(String header) {
		if (header.startsWith("Bearer ")) {
			return Mono.just(header.substring(7));
		} else if (header.length() > 0) {
			return Mono.just(header);
		}
		return Mono.empty();
	}
}
