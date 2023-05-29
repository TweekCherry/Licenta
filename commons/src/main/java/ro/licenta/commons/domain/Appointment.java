package ro.licenta.commons.domain;

import java.time.LocalDateTime;

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
	
	public static final String KEY_SPACE = "appoiments";

	@Id
	private String id;
	private String user;
	private LocalDateTime timestmap;
	private Medic medic;
	private Clinic clinic;
	private Investigation investigation;
	private AppoimentStatus status = AppoimentStatus.SCHEDULED;
	
	public static enum AppoimentStatus {
		SCHEDULED, IN_PROGRESS, FINISHED
	}
}
