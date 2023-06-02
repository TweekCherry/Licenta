package ro.licenta.medici.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.repository.AppointmentRepository;

@RestController
@RequestMapping("/appointments")
public class AppointmentsController extends DefaultController {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@GetMapping
	public Mono<List<Appointment>> find() {
		return super.getCurrentUser().flatMapMany(apiToken -> {
			return appointmentRepository.aggregateAll(apiToken.getUser());
		}).collectList();
	}
	
}
