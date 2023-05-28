package ro.licenta.commons.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(Departament.KEY_SPACE)
public class Departament {

	public static final String KEY_SPACE = "departments";
	
	@Id
	private String name;
	
}
