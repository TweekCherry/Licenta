package ro.licenta.administrare.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Clinic;
import ro.licenta.commons.repository.ClinicRepository;
import ro.licenta.commons.repository.InvestigationRepository;
import ro.licenta.commons.repository.MedicRepository;

@RestController
@RequestMapping("/clinic")
public class ClinicController extends DefaultController {

	@Autowired
	private ClinicRepository clinicRepository;
	@Autowired
	private MedicRepository medicRepository;
	@Autowired
	private InvestigationRepository investigationRepository;
	
	@GetMapping
	public Mono<List<Clinic>> findAll() {
		return clinicRepository.findAll().collectList();
	}
	
	@PostMapping
	public Mono<Clinic> save(@RequestBody Clinic clinic) {
		return clinicRepository.save(clinic);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") ObjectId id) {
		return clinicRepository.deleteById(id)
			.then(medicRepository.unlinkClinic(id))
			.then(investigationRepository.unlinkClinic(id))
			.then();
	}
	
}
