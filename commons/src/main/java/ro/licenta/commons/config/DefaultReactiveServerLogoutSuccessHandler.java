package ro.licenta.commons.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import ro.licenta.commons.config.notifications.VapidSessionRegistry;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.service.ReactiveAutenticationService;

@Component
public class DefaultReactiveServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {

	@Autowired
	private ReactiveAutenticationService reactiveAutenticationService;
	@Autowired(required = false)
	protected VapidSessionRegistry vapidSessionRegistry;
	
	/** 
	 * (non-Javadoc)
	 * @see org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler#onLogoutSuccess(org.springframework.security.web.server.WebFilterExchange, org.springframework.security.core.Authentication)
	 */
	@Override
	public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
		if (authentication.getPrincipal() instanceof ApiToken) {
			ApiToken apiToken = (ApiToken) authentication.getPrincipal();
			if (vapidSessionRegistry != null) {
				vapidSessionRegistry.removeSession(apiToken).flatMap(s->{
					s.cancelSubscription(apiToken.getKey());
					return Mono.empty();
				}).subscribe();
			}
			
			return reactiveAutenticationService.clearApiToken(apiToken.getKey())
				.then(Mono.fromRunnable(() -> {
					ServerHttpResponse response = exchange.getExchange().getResponse();
					response.setStatusCode(HttpStatus.ACCEPTED);
//					response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
				}));
		}
		return Mono.empty();
	}

}
