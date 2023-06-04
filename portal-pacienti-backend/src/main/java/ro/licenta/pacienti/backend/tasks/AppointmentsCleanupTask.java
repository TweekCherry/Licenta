package ro.licenta.pacienti.backend.tasks;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.domain.Appointment.AppointmentStatus;
import ro.licenta.commons.repository.AppointmentRepository;

@Log4j2
@Component
public class AppointmentsCleanupTask {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	/**
	 * Update the appointments status every hour and 5 minutes
	 * */
	@Scheduled(cron = "0 10 * * * *")
	private void cleanupAppointments() {
		log.info("Appointment cleanup started");
		appointmentRepository.findAll()
			.filter(this::isInvalid)
			.map(a -> a.setStatus(AppointmentStatus.NOT_PRESENTED))
			.flatMap(appointmentRepository::save)
			.subscribe(a -> log.info("Appointment cancelled: {}", a.getId()));
	}
	
	private Boolean isInvalid(Appointment appointment) {
		return appointment.getTimestamp().isBefore(LocalDateTime.now()) && 
			appointment.getStatus().equals(AppointmentStatus.SCHEDULED);
	}
}
