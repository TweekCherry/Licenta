package ro.licenta.commons.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
@Document(Investigation.KEY_SPACE)
public class Investigation {
	
	public static final String KEY_SPACE = "investigations";
	
	@Id
	private ObjectId id;
	private String name;
	private Float price;
	private String department;
	private String type;
	private Set<ObjectId> clinics = new HashSet<>();
	private List<Clinic> clinicsData = new ArrayList<>();
}
