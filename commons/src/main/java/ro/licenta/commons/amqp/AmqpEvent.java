package ro.licenta.commons.amqp;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AmqpEvent<T extends Serializable> implements Serializable, Event {
	private static final long serialVersionUID = 2869353439818412077L;
	
	private String type;
	private T payload;
	
	public AmqpEvent(T payload) {
		this.type = payload.getClass().getSimpleName();
		this.payload = payload;
	}
	
}
