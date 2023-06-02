package ro.licenta.pacienti.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.repository.AppointmentRepository;

@RestController
@RequestMapping("/appointments")
public class AppointmentController extends DefaultController {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@GetMapping
	public Mono<List<Appointment>> findForUser() {
		return super.getCurrentUser()
			.flatMapMany(apiToken -> appointmentRepository.aggregateByUser(apiToken.getUser()))
			.collectList();
	}
	
	@PostMapping
	public Mono<Appointment> save(@RequestBody Appointment appointment) {
		return super.getCurrentUser()
			.flatMap(apiToken -> appointmentRepository.save(appointment.setUser(apiToken.getUser())));
	}
	
	@GetMapping("/validate")
	public Mono<Boolean> validateAppointmentData(@RequestParam("date") LocalDateTime timestmap, @RequestParam("medic") ObjectId medic) {
		return appointmentRepository.existsByMedicAndTimestamp(medic, timestmap);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") ObjectId id) {
		return super.getCurrentUser().flatMap(apiToken -> {
			return appointmentRepository.deleteByIdAndUser(id, apiToken.getUser());
		});
	}
	
	@GetMapping("/check-dates")
	public Mono<List<LocalDateTime>> findBookedAppointmentDates(
		@RequestParam("clinic") ObjectId clinic,
		@RequestParam("medic") ObjectId medic) {
		return appointmentRepository.findBookedAppointmentDates(clinic, medic);
	}
	
}
