package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
	private ObjectId id;
	private ObjectId user;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private LocalDate dateOfBirth;
	private String cnp;
	private String gender;
	private Long age;
	@Transient
	private Address address; // we don't need address anymore
	private Subscription subscription;
	private LocalDateTime subscriptionDate;
	private LocalDateTime subscriptionExpirationDate;
	
	public String fullName() {
		return firstName +  " " + lastName;
	}

	public boolean isSubscriptionValid() {
		if (this.subscription != null && this.subscriptionExpirationDate.isBefore(LocalDateTime.now())) {
			return true;
		}
		return false;
	}
	
}
