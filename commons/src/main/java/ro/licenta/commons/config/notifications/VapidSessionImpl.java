package ro.licenta.commons.config.notifications;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class VapidSessionImpl implements VapidSession {

	private String id;
	private String endpoint;
	private String auth;
	private String p256DH;

}
