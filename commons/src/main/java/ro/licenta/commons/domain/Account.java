package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(Account.KEY_SPACE)
public class Account implements Serializable {
	private static final long serialVersionUID = -4462979946943376231L;

	public static final String KEY_SPACE = "accounts";

	@Id
	private String id;
	private String username;
	private String password;
	private String email;
	private String phoneNumber;
	private LocalDate createDate;
	private String token;

	private Set<String> roles = new HashSet<>();

}
