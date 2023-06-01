package ro.licenta.commons.domain;

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
@Document(Medic.KEY_SPACE)
public class Medic {

	public static final String KEY_SPACE = "medics";
	
	@Id
	private ObjectId id;
	private String image; // base64 image
	private String title;
	private String grade;
	private Set<String> departments = new HashSet<>();
	private ObjectId clinic;
	private Boolean deleted = false;
	
	private Profile profileData;
	private Clinic clinicData;
	private Account accountData;
}
