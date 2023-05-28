package ro.licenta.commons.domain;

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
@Document(InvestigationType.KEY_SPACE)
public class InvestigationType {
	
	public static final String KEY_SPACE = "investigations-type";

	@Id
	private String name;
	
}
