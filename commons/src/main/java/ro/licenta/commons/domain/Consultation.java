package ro.licenta.commons.domain;

import java.time.LocalDate;

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
@Document(Consultation.KEY_SPACE)
public class Consultation {
	
	public static final String KEY_SPACE = "consultations";

	@Id
	private ObjectId id;
	private ObjectId user;
	private LocalDate date;
	private ObjectId clinic;
	private ObjectId medic;
	private String diagnostic;
	private Prescription prescription;
}
