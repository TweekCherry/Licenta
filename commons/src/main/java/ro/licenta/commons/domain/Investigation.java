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
@Document(Investigation.KEY_SPACE)
public class Investigation {
	
	public static final String KEY_SPACE = "investigations";
	
	@Id
	private String id;
	private String name;
	private Float price;
	private String category;
	private String type;
}
