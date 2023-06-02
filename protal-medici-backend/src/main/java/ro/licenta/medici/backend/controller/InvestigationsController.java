package ro.licenta.medici.backend.controller;

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

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Investigation;
import ro.licenta.commons.domain.InvestigationType;
import ro.licenta.commons.repository.InvestigationRepository;
import ro.licenta.commons.repository.InvestigationTypeRepository;
import ro.licenta.commons.repository.SubscriptionRepository;

@RestController
@RequestMapping("/investigations")
public class InvestigationsController extends DefaultController {

	@Autowired
	private SubscriptionRepository subscriptionRepository;
	@Autowired
	private InvestigationRepository investigationRepository;
	@Autowired
	private InvestigationTypeRepository investigationTypeRepository;
	
	@GetMapping
	public Mono<List<Investigation>> findAll() {
		return investigationRepository.aggregateAll();
	}
	
	@PostMapping
	public Mono<Investigation> save(@RequestBody Investigation subscription) {
		return investigationRepository.save(subscription);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") ObjectId id) {
		return investigationRepository.deleteById(id)
			.then(subscriptionRepository.unlinkInvestigation(id))
			.then();
	}
	
	@GetMapping("/types")
	public Mono<List<InvestigationType>> findAllTypes() {
		return investigationTypeRepository.findAll().collectList();
	}
	
	@PostMapping("/types")
	public Mono<InvestigationType> saveType(@RequestParam(name = "id", required = false) String id, @RequestBody InvestigationType investigationType) {
		Mono<UpdateResult> deleteInvestigation = Mono.empty();
		if (id != null) {
			deleteInvestigation = investigationTypeRepository.deleteById(id)
					.then(investigationRepository.replaceType(id, investigationType.getName()));
		}
		return deleteInvestigation
			.then(investigationTypeRepository.save(investigationType));
	}
	
	@DeleteMapping("/types/{id}")
	public Mono<Void> deleteType(@PathVariable("id") String id) {
		return investigationTypeRepository.deleteById(id)
			.then(investigationRepository.unlinkType(id))
			.then();
	}
}
