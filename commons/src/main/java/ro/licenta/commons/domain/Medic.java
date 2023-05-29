package ro.licenta.commons.domain;

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
@Document(Medic.KEY_SPACE)
public class Medic {

	public static final String KEY_SPACE = "medics";
	
	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String profile; // base64 image
	private String title;
	private Grade grade;
	private Set<Departament> departments = new HashSet<>();
	private String clinic;
}
