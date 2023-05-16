/**
 * 
 */
package ro.licenta.commons.config.notifications;

import java.io.Serializable;

/**
 * @author r.m.ghimis
 *
 */
public interface Event extends Serializable {

	public String getType();
	
}
