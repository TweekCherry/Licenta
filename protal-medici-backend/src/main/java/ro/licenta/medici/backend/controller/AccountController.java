package ro.licenta.medici.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Medic;
import ro.licenta.commons.domain.Profile;
import ro.licenta.commons.repository.ClinicRepository;
import ro.licenta.commons.repository.MedicRepository;
import ro.licenta.commons.repository.ProfileRepository;

@RestController
@RequestMapping("/account")
public class AccountController extends DefaultController {

	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private MedicRepository medicRepository;
	@Autowired
	private ClinicRepository clinicRepository;
	
	@GetMapping("/profile")
	public Mono<Profile> findUserProfile() {
		return super.getCurrentUser()
			.flatMap(apiToken -> profileRepository.findByUser(apiToken.getUser()))
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Profile not found")));
	}
	
	@GetMapping("/medic")
	public Mono<Medic> findMedicProfile() {
		return super.getCurrentUser()
			.flatMap(apiToken -> medicRepository.findById(apiToken.getUser()))
			.flatMap(medic -> clinicRepository.findById(medic.getClinic())
				.map(medic::setClinicData)
			)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Profile not found")));
	}
	
}
