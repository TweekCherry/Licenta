package ro.licenta.commons.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

	protected String firstName;
	protected String lastName;
	protected String email;
	protected String password;
	protected String phoneNumber;
	protected String cnp;
	protected String city;
	protected String county;
	protected String street;
	protected String number;
	protected String details;
	
}
