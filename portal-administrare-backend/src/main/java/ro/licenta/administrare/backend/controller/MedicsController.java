package ro.licenta.administrare.backend.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.repository.AccountRepository;
import ro.licenta.commons.repository.ApiTokenRepository;
import ro.licenta.commons.repository.AppointmentRepository;
import ro.licenta.commons.repository.MedicRepository;
import ro.licenta.commons.repository.ProfileRepository;
import ro.licenta.commons.requests.MedicRegistrationRequest;
import ro.licenta.commons.requests.MedicUpdateRequest;

@RestController
@RequestMapping("/medics")
public class MedicsController extends DefaultController {

	@Autowired
	private MedicRepository medicRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ApiTokenRepository apiTokenRepository;
	
	@GetMapping
	public Mono<List<Medic>> findAll() {
		return medicRepository.aggregateAll().collectList();
	}
	
	@PostMapping
	public Mono<Medic> save(@RequestBody MedicRegistrationRequest request) {
		request.setId(new ObjectId()); // we share the id between medic, profile and account
		request.getAccount().setPassword(passwordEncoder.encode(request.getPassword()));
		return accountRepository.existsByEmail(request.getAccount().getEmail())
			.filter(exists -> exists)
			.flatMap(e -> Mono.error(new IllegalArgumentException("Email already exists")))
			.then(Mono.defer(() -> Mono.zip(
				accountRepository.save(request.getAccount()),
				profileRepository.save(request.getProfile()),
				medicRepository.save(request.getMedic())
			))).map(Tuple3::getT3);
	}
	
	@PutMapping
	public Mono<Medic> update(@RequestBody MedicUpdateRequest request) {
		if (request.getPassword() != null) {
			request.getAccount().setPassword(passwordEncoder.encode(request.getPassword()));// replace password
		}
		return accountRepository.existsByEmailAndIdNot(request.getAccount().getEmail(), request.getAccount().getId())
			.filter(exists -> exists)
			.flatMap(e -> Mono.error(new IllegalArgumentException("Email already exists")))
			.then(Mono.defer(() -> Mono.zip(
				profileRepository.save(request.getProfile()),
				accountRepository.save(request.getAccount()),
				medicRepository.save(request.getMedic())
			))).map(Tuple3::getT3);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") ObjectId id) {
		return medicRepository.findById(id)
			.map(medic -> medic.setDeleted(true))
			.flatMap(medicRepository::save)
			.flatMap(medic -> accountRepository.findById(medic.getId()))
			.flatMap(a -> accountRepository.save(a.setDeleted(true)))
			.flatMap(a -> apiTokenRepository.deleteAllByUser(a.getId()))
			.then(appointmentRepository.cancelByMedic(id))
			.then();
	}
	
}
