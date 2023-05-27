package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDate;

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
@Document(Profile.KEY_SPACE)
public class Profile implements Serializable {
	private static final long serialVersionUID = -4462979946943376231L;

	public static final String KEY_SPACE = "profile";

	@Id
	private String id;
	private String user;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private LocalDate dateOfBirth;
	private String cnp;
	private String gender;
	private Integer age;
	private Address address;

}
