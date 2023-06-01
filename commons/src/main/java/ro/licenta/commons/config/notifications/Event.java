/**
 * 
 */
package ro.licenta.commons.config.notifications;

import java.io.Serializable;

public interface Event extends Serializable {

	public String getType();
	
}
