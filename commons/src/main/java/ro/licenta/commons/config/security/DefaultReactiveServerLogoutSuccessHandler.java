package ro.licenta.commons.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.repository.ApiTokenRepository;

@Component
public class DefaultReactiveServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {

	@Autowired
	private ApiTokenRepository apiTokenRepository;
	
	/** 
	 * (non-Javadoc)
	 * @see org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler#onLogoutSuccess(org.springframework.security.web.server.WebFilterExchange, org.springframework.security.core.Authentication)
	 */
	@Override
	public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
		if (authentication.getPrincipal() instanceof ApiToken) {
			ApiToken apiToken = (ApiToken) authentication.getPrincipal();
			
			return apiTokenRepository.deleteByKey(apiToken.getKey())
				.then(Mono.fromRunnable(() -> {
					ServerHttpResponse response = exchange.getExchange().getResponse();
					response.setStatusCode(HttpStatus.ACCEPTED);
				}));
		}
		return Mono.empty();
	}

}
