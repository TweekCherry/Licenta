package ro.licenta.commons.domain;

import java.util.ArrayList;
import java.util.List;

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
	
	private String id;
	private String firstName;
	private String lastName;
	private String profile; // base64 image
	private String title;
	private String grade;
	private List<Departament> departments = new ArrayList<>();
}
