package ro.licenta.commons.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.domain.Profile;

@Getter
@Setter
@NoArgsConstructor
public class MedicUpdateRequest {
	
	private Medic medic;
	private Account account;
	private Profile profile;
	private String password;
}
