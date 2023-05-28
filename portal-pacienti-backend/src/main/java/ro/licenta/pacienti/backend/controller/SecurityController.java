/**
 * 
 */
package ro.licenta.pacienti.backend.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.Address;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.domain.Profile;
import ro.licenta.commons.domain.Roles;
import ro.licenta.commons.repository.AccountRepository;
import ro.licenta.commons.repository.ApiTokenRepository;
import ro.licenta.commons.repository.ProfileRepository;
import ro.licenta.commons.requests.BasicAuthenticationRequest;
import ro.licenta.commons.requests.ForgotPasswordRequest;
import ro.licenta.commons.requests.RegisterRequest;
import ro.licenta.commons.service.MailService;

@Log4j2
@RestController
public class SecurityController {

	@Autowired
	private ApiTokenRepository apiTokenRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MailService mailService;
	
	@PostConstruct
	public void postConstruct() {
		RegisterRequest request = new RegisterRequest();
		request.setFirstName("Admin");
		request.setLastName("");
		request.setEmail("albert@shield-solutions.com");
		request.setPassword("admin");
		request.setPhoneNumber("");
		request.setCnp("1710305418106");
		request.setCity("");
		request.setCounty("");
		request.setStreet("");
		request.setNumber("");
		request.setDetails("");
		this.register("ADMIN_ACCOUNT", request)
			.onErrorContinue((e, o) -> log.warn("Administrator account already created"))
			.doOnSuccess(v -> log.info("Administrator account created, check email"))
			.subscribe();
	}
	
	@PostMapping("/login")
	public Mono<ApiToken> login(@RequestBody BasicAuthenticationRequest loginRequest) {
		return accountRepository.findByEmail(loginRequest.getEmail())
			.filter(account -> passwordEncoder.matches(loginRequest.getPassword(), account.getPassword()))
			.map(account -> new ApiToken(account, loginRequest.getRememberMe()))
			.flatMap(apiTokenRepository::save)
			.switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials")));
	}
	
	@PostMapping("/forgot-password")
	public Mono<Void> forgotPassword(@RequestParam("url") String redirectUrl, @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		return accountRepository.findByEmail(forgotPasswordRequest.getEmail())
			.flatMap(account -> accountRepository.save(account.setToken(UUID.randomUUID().toString())))
			.flatMap(account -> {
				Map<String, String> content = new HashMap<>();
				content.put("firstName", account.getEmail());
				content.put("url", redirectUrl+"?token="+account.getToken());
				return mailService.sendEmail(account.getEmail(), "Password recovery", "recovery.html", content);
			});
	}
	
	@PostMapping("/register")
	public Mono<Void> register(@RequestParam("url") String redirectUrl, @RequestBody RegisterRequest registerRequest) {
		return accountRepository.existsByEmail(registerRequest.getEmail())
			.filter(exists -> !exists)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Email already exists")))
			.flatMap(r -> {
				Account account = new Account();
				account.setEmail(registerRequest.getEmail());
				account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
				account.setCreateDate(LocalDate.now());
				account.setToken(UUID.randomUUID().toString());
				account.getRoles().add(Roles.ROLE_CLIENT);
				return accountRepository.save(account).flatMap(a -> { // save account then
					Profile profile = new Profile();
					profile.setUser(a.getId());
					profile.setFirstName(registerRequest.getFirstName());
					profile.setLastName(registerRequest.getLastName());
					profile.setPhoneNumber(registerRequest.getPhoneNumber());
					
					LocalDate dateOfBirth = LocalDate.of(
						Integer.parseInt(registerRequest.getCnp().substring(1, 3)), // year
						Integer.parseInt(registerRequest.getCnp().substring(3, 5)), // month
						Integer.parseInt(registerRequest.getCnp().substring(5, 7)) // day
					);
					long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
					profile.setAge(age);
					profile.setDateOfBirth(dateOfBirth);
					profile.setCnp(registerRequest.getCnp());
					profile.setGender(registerRequest.getGender());
					profile.setAddress(new Address());
					profile.getAddress().setCity(registerRequest.getCity());
					profile.getAddress().setCounty(registerRequest.getCounty());
					profile.getAddress().setStreet(registerRequest.getStreet());
					profile.getAddress().setNumber(registerRequest.getNumber());
					profile.getAddress().setDetails(registerRequest.getDetails());
					return profileRepository.save(profile).then(Mono.defer(() -> { // save profile then
						Map<String, String> content = new HashMap<>();
						content.put("firstName", profile.getFirstName());
						content.put("url", redirectUrl+"?token="+account.getToken());
						return mailService.sendEmail(a.getEmail(), "Registration", "register.html", content); // send registration email
					}));
				});
			});
	}
	
	@PutMapping("/forgot-password")
	public Mono<ApiToken> completeForgotPassword(@RequestParam("token") String token, @RequestBody String password) {
		return accountRepository.findByToken(token)
			.flatMap(account -> {
				account.setToken(null);
				account.setPassword(passwordEncoder.encode(password));
				return accountRepository.save(account);
			})
			.map(account -> new ApiToken(account, false))
			.flatMap(apiTokenRepository::save)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid token")));
	}
	
	@PutMapping("/register")
	public Mono<ApiToken> completeRegister(@RequestParam("token") String token) {
		return accountRepository.findByToken(token)
			.flatMap(account -> {
				account.setToken(null);
				return accountRepository.save(account);
			})
			.map(account -> new ApiToken(account, false))
			.flatMap(apiTokenRepository::save)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid token")));
	}
}
