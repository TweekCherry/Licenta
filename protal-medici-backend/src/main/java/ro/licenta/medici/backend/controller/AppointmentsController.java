package ro.licenta.medici.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.amqp.AppointmentCancelled;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.domain.Appointment;
import ro.licenta.commons.domain.Appointment.AppointmentStatus;
import ro.licenta.commons.domain.Consultation;
import ro.licenta.commons.domain.Prescription;
import ro.licenta.commons.repository.AppointmentRepository;
import ro.licenta.commons.repository.ConsultationRepository;
import ro.licenta.medici.backend.components.AmqpListener;

@RestController
@RequestMapping("/appointments")
public class AppointmentsController extends DefaultController {

	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private ConsultationRepository consultationRepository;
	@Autowired
	private AmqpListener amqpListener;
	
	@GetMapping
	public Mono<List<Appointment>> find(@RequestParam(name = "onlyScheduled", required=false) Boolean onlyScheduled) {
		return super.getCurrentUser().flatMapMany(apiToken -> {
			AppointmentStatus[] statuses = new AppointmentStatus[0];
			if (onlyScheduled) {
				statuses = new AppointmentStatus[] { AppointmentStatus.SCHEDULED, AppointmentStatus.IN_PROGRESS };
			}
			return appointmentRepository.aggregateByMedic(apiToken.getUser(), statuses);
		}).collectList();
	}
	
	@PutMapping("/{id}")
	public Mono<Appointment> cancel(@PathVariable("id") ObjectId id) {
		return super.getCurrentUser()
			.flatMap(apiToken -> appointmentRepository.findById(id))
			.map(appointment -> appointment.setStatus(AppointmentStatus.CANCELLED))
			.flatMap(appointmentRepository::save)
			.doOnNext(a -> amqpListener.notifyPatients(new AppointmentCancelled(a)));
	}
	
	@PostMapping("/{id}")
	public Mono<Consultation> start(@PathVariable("id") ObjectId id) {
		return super.getCurrentUser()
			.filterWhen(this::hasNoActiveAppointment)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("You already have an active consultation")))
			.flatMap(apiToken -> appointmentRepository.aggregateById(id))
			.map(appointment -> appointment.setStatus(AppointmentStatus.IN_PROGRESS))
			.flatMap(appointmentRepository::save)
			.flatMap(a -> {
				Consultation consultation = new Consultation();
				consultation.setPrescription(new Prescription());
				consultation.setAppointment(a);
				return consultationRepository.save(consultation);
			});
	}
	
	private Mono<Boolean> hasNoActiveAppointment(ApiToken apiToken) {
		return appointmentRepository.findByMedicAndStatus(apiToken.getUser(), AppointmentStatus.IN_PROGRESS) // find the active appointment
			.map(a -> true) // true means we found one
			.switchIfEmpty(Mono.just(false)) // false means we didn't found any
			.filter(b -> b == false); // check if we didn't find any
	}
}
