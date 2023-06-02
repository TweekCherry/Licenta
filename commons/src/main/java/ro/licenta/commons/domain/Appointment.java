package ro.licenta.commons.domain;

import java.time.LocalDateTime;

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
@Document(Appointment.KEY_SPACE)
public class Appointment {
	
	public static final String KEY_SPACE = "appointments";

	@Id
	private ObjectId id;
	private ObjectId user;
	private LocalDateTime timestamp;
	private ObjectId medic;
	private ObjectId clinic;
	private ObjectId investigation;
	private AppoimentStatus status = AppoimentStatus.SCHEDULED;
	
	private Profile userData;
	private Clinic clinicData;
	private Medic medicData;
	private Investigation investigationData;
	
	public static enum AppoimentStatus {
		SCHEDULED, FINISHED, CANCELLED
	}
}
