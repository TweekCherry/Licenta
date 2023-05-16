package ro.licenta.commons.config.notifications;

import java.io.Serializable;

public interface SessionHandlerSubscription extends Serializable {

	public String getKey();

	public void cancel();
	
}
