package ro.licenta.medici.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.amqp.AppointmentFinished;
import ro.licenta.commons.domain.Appointment.AppointmentStatus;
import ro.licenta.commons.domain.Consultation;
import ro.licenta.commons.repository.AppointmentRepository;
import ro.licenta.commons.repository.ConsultationRepository;
import ro.licenta.medici.backend.components.AmqpListener;

@RestController
@RequestMapping("/consultations")
public class ConsultationsController extends DefaultController {

	@Autowired
	private ConsultationRepository consultationRepository;
	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private AmqpListener amqpListener;
	
	@GetMapping
	public Mono<List<Consultation>> find() {
		return super.getCurrentUser().flatMapMany(apiToken -> {
			return consultationRepository.findByAppointmentMedic(apiToken.getUser());
		}).collectList();
	}
	
	@GetMapping("/active")
	public Mono<Consultation> activeConsultation() {
		return super.getCurrentUser()
			.flatMap(apiToken -> consultationRepository.findByAppointmentMedicAndAppointmentStatus(apiToken.getUser(), AppointmentStatus.IN_PROGRESS));
	}
	
	@PostMapping
	public Mono<Consultation> finish(@RequestBody Consultation consultation) {
		consultation.getAppointment().setStatus(AppointmentStatus.FINISHED);
		return consultationRepository.save(consultation).flatMap(c -> {
			return appointmentRepository.findById(c.getAppointment().getId())
				.map(a -> a.setStatus(AppointmentStatus.FINISHED))
				.flatMap(appointmentRepository::save)
				.doOnNext(a -> amqpListener.notifyPatients(new AppointmentFinished(a)))
				.doOnNext(a -> amqpListener.getInbound().tryEmitNext(new AppointmentFinished(a))) // send also a notification to self
				.thenReturn(c);
		});
	}
}
