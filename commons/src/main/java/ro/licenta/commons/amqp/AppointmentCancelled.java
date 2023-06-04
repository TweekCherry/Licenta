package ro.licenta.commons.amqp;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.licenta.commons.domain.Appointment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCancelled implements Serializable {
	private static final long serialVersionUID = -7136104599890316304L;
	
	private Appointment appointment;
	
}
