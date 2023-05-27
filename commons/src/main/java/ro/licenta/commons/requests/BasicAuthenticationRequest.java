package ro.licenta.commons.requests;

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

	protected String email;
	protected String password;
	protected Boolean rememberMe = false;
	
}
