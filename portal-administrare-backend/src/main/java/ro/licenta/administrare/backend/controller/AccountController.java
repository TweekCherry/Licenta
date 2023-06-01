package ro.licenta.administrare.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Profile;
import ro.licenta.commons.repository.ProfileRepository;

@RestController
@RequestMapping("/account")
public class AccountController extends DefaultController {

	@Autowired
	private ProfileRepository profileRepository;
	
	@GetMapping("/profile")
	public Mono<Profile> findUserProfile() {
		return super.getCurrentUser()
			.flatMap(apiToken -> profileRepository.findByUser(apiToken.getUser()))
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Profile not found")));
	}
	
}
