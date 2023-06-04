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
public class AppointmentStarted implements Serializable {
	private static final long serialVersionUID = -4028044619760570418L;
	
	private Appointment appointment;
}
