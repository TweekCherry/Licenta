package ro.licenta.pacienti.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.repository.MedicRepository;

@RestController
@RequestMapping("/medics")
public class MedicsController extends DefaultController {

	@Autowired
	private MedicRepository medicRepository;
	
	@GetMapping
	public Mono<List<Medic>> findMedics() {
		return medicRepository.aggregateAll().collectList();
	}
	
	@GetMapping("/clinic/{id}")
	public Mono<List<Medic>> findMedicsByClinic(@PathVariable("id") ObjectId clinic) {
		return medicRepository.aggregateAllByClinic(clinic).collectList();
	}
}
