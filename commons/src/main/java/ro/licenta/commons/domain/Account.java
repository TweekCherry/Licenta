package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
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
	private ObjectId id;
	private String email;
	private String password;
	private LocalDate createDate;
	private String token;
	private Boolean deleted = false;
	private Set<String> roles = new HashSet<>();

}
