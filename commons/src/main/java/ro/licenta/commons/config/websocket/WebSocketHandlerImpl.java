/**
 * 
 */
package ro.licenta.commons.config.websocket;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;

@Component
public class WebSocketHandlerImpl implements WebSocketHandler {
	
	@Autowired
	private ObjectProvider<WebSocketSessionHandler> webSocketSessionHandlerProvider;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.reactive.socket.WebSocketHandler#handle(org.springframework.web.reactive.socket.WebSocketSession)
	 */
	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.getHandshakeInfo().getPrincipal()
		.map(UsernamePasswordAuthenticationToken.class::cast)
		.map(auth -> auth.getPrincipal())
		.map(ApiToken.class::cast)
		.map(apiToken -> webSocketSessionHandlerProvider.getObject(apiToken, session))
		.flatMap(WebSocketSessionHandler::connect);
	}
	
}
