package ro.licenta.pacienti.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Consultation;
import ro.licenta.commons.repository.ConsultationRepository;

@RestController
public class ConsultationsController extends DefaultController {

	@Autowired
	private ConsultationRepository consultationRepository;
	
	@GetMapping
	public Mono<List<Consultation>> findByUser() {
		return super.getCurrentUser()
			.flatMapMany(apiToken -> consultationRepository.findByUser(apiToken.getUser()))
			.collectList();
	}
	
}
