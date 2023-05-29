package ro.licenta.pacienti.backend.controller;

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
public class AppointmentController extends DefaultController {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@GetMapping
	public Mono<List<Appointment>> findForUser(){
		return super.getCurrentUser()
			.flatMapMany(apiToken -> appointmentRepository.findByUser(apiToken.getUser()))
			.collectList();
	}
	
}
