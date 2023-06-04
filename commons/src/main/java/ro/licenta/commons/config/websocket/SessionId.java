package ro.licenta.commons.config.websocket;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class SessionId implements Serializable {
	private static final long serialVersionUID = -1041491112100944095L;
	
	private final String id;
	
	public SessionId(String id) {
		this.id = id;
	}
	
}
