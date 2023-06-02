package ro.licenta.commons.requests;

import java.time.LocalDate;
import java.util.UUID;

import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.domain.Profile;
import ro.licenta.commons.domain.Roles;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class MedicRegistrationRequest {

	private ObjectId id;
	private Medic medic;
	private Account account;
	private Profile profile;
	private String password;
	
	public Medic getMedic() {
		medic.setId(this.id);
		return medic;
	}
	
	public Profile getProfile() {
		profile.setId(this.id);
		profile.setUser(this.id);
		return profile;
	}
	
	public Account getAccount() {
		account.setId(this.id);
		account.setCreateDate(LocalDate.now());
		account.setToken(UUID.randomUUID().toString());
		account.getRoles().add(Roles.ROLE_MEDIC);
		return account;
	}
}
