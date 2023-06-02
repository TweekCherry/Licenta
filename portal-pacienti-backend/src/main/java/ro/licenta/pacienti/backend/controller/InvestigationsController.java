package ro.licenta.pacienti.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Investigation;
import ro.licenta.commons.repository.InvestigationRepository;

@RestController
@RequestMapping("/investigations")
public class InvestigationsController extends DefaultController {

	@Autowired
	private InvestigationRepository investigationRepository;
	
	@GetMapping
	public Mono<List<Investigation>> findAllInvestigations() {
		return investigationRepository.aggregateAll();
	}
	
	@GetMapping("/clinic/{id}")
	public Mono<List<Investigation>> findByClinic(@PathVariable("id") ObjectId clinic) {
		return investigationRepository.findByClinics(clinic).collectList();
	}
}
