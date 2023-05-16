package ro.licenta.commons;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class BasicAuthenticationRequest implements Serializable {
	private static final long serialVersionUID = -4626947796060208661L;

	protected String username;
	protected String password;
	
	public BasicAuthenticationRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
}
