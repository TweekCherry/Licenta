package ro.licenta.commons.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(Departament.KEY_SPACE)
public class Departament {

	public static final String KEY_SPACE = "departments";
	
	@Id
	private String name;
	
}
