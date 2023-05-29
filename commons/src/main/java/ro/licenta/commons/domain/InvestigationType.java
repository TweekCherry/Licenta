package ro.licenta.commons.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(InvestigationType.KEY_SPACE)
public class InvestigationType {
	
	public static final String KEY_SPACE = "investigation-types";

	@Id
	private String name;
	
}
