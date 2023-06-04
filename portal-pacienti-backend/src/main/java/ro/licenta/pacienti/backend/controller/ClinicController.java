package ro.licenta.pacienti.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.repository.ClinicRepository;

@RestController
@RequestMapping("/clinic")
public class ClinicController extends DefaultController {

	@Autowired
	private ClinicRepository clinicRepository;
	
	@GetMapping
	public Mono<List<Clinic>> findAll() {
		return clinicRepository.findAll().collectList();
	}
	
	@GetMapping("/investigations/{id}")
	public Mono<List<Clinic>> findByInvestigation(@PathVariable("id") ObjectId id) {
		return clinicRepository.findByInvestigationId(id);
	}
	
}
