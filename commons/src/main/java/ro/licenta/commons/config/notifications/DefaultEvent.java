/**
 * 
 */
package ro.licenta.commons.config.notifications;

public class DefaultEvent<T> implements Event {
	private static final long serialVersionUID = 5387966155578542719L;
	
	private String type;
	private T payload;
	
	public DefaultEvent(T payload, String type) {
		this(payload);
		this.type = type;
	}
	
	public DefaultEvent(T payload) {
		this.payload = payload;
		if (payload != null) {
			this.type = payload.getClass().getSimpleName();
		} else {
			this.type = Void.class.getSimpleName();
		}
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public T getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "DefaultEvent [type=" + type + ", payload=" + payload + "]";
	}
	
}
