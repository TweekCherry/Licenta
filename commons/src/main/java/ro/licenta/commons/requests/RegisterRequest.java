package ro.licenta.commons.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phoneNumber;
	private String cnp;
	private String city;
	private String county;
	private String street;
	private String number;
	private String details;
	
}
