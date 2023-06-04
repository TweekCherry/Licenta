/**
 * 
 */
package ro.licenta.commons.config.websocket;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import reactor.core.Disposable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WebSocketSubscription implements Serializable {
	private static final long serialVersionUID = 3076218669878722041L;
	
	@Getter
	@EqualsAndHashCode.Include
	private final String key;
	@JsonIgnore
	private final Disposable disposable;
	
	public WebSocketSubscription(Disposable disposable) {
		this.disposable = disposable;
		this.key = UUID.randomUUID().toString();
	}

	public void cancel() {
		disposable.dispose();
	}
}
