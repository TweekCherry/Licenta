package ro.licenta.commons.config.security;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Order(-2) // above DefaultErrorWebExceptionHandler
@Component
public class AbstractSecurityExceptionHandler extends AbstractErrorWebExceptionHandler {

	@Autowired
	private ServerCodecConfigurer serverCodecConfigurer;
	
	public AbstractSecurityExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties, ApplicationContext applicationContext) {
		super(errorAttributes, webProperties.getResources(), applicationContext);
	}
	
	@PostConstruct
	public void postContruct() {
		this.setMessageWriters(serverCodecConfigurer.getWriters());
		this.setMessageReaders(serverCodecConfigurer.getReaders());
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}
	
	private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		Throwable currentError = getError(request);
		log.error("Exception occured for request {} {}",request.path(), currentError.getMessage());
		if (currentError instanceof LockedException) {
			return ServerResponse.status(HttpStatus.LOCKED)
				.header("Access-Control-Allow-Origin", "*")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(currentError.getMessage());
		}
		Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.of(Include.STACK_TRACE, Include.MESSAGE));
		return ServerResponse.status(HttpStatus.BAD_REQUEST)
				.header("Access-Control-Allow-Origin", "*")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(errorPropertiesMap));
	}
}
