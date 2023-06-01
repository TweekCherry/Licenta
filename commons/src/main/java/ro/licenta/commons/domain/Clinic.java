package ro.licenta.commons.domain;

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
@Document(Clinic.KEY_SPACE)
public class Clinic {

	public static final String KEY_SPACE = "clinics";
	
	@Id
	private ObjectId id;
	private String name;
	private String phoneNumber;
	private Address address = new Address();
	
}
