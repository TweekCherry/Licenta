/**
 * 
 */
package ro.licenta.pacienti.backend.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
				ObjectId id = new ObjectId();
				Account account = new Account();
				account.setId(id);
				account.setEmail(registerRequest.getEmail());
				account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
				account.setCreateDate(LocalDate.now());
				account.setToken(UUID.randomUUID().toString());
				account.getRoles().add(Roles.ROLE_CLIENT);
				
				Profile profile = new Profile();
				profile.setId(id);
				profile.setUser(id);
				profile.setFirstName(registerRequest.getFirstName());
				profile.setLastName(registerRequest.getLastName());
				profile.setPhoneNumber(registerRequest.getPhoneNumber());
				
				String male = "135";
				String female = "246";
				if (male.contains("" + registerRequest.getCnp().charAt(0))) {
					profile.setGender("Male");
				} else if (female.contains("" + registerRequest.getCnp().charAt(0))) {
					profile.setGender("Female");
				}
				
				LocalDate dateOfBirth = LocalDate.of(
					Integer.parseInt(registerRequest.getCnp().substring(1, 3)), // year
					Integer.parseInt(registerRequest.getCnp().substring(3, 5)), // month
					Integer.parseInt(registerRequest.getCnp().substring(5, 7)) // day
				);
				long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
				profile.setAge(age);
				profile.setDateOfBirth(dateOfBirth);
				profile.setCnp(registerRequest.getCnp());
				profile.setAddress(new Address());
				profile.getAddress().setCity(registerRequest.getCity());
				profile.getAddress().setCounty(registerRequest.getCounty());
				profile.getAddress().setStreet(registerRequest.getStreet());
				profile.getAddress().setNumber(registerRequest.getNumber());
				profile.getAddress().setDetails(registerRequest.getDetails());
				return Mono.when(
					profileRepository.save(profile),
					accountRepository.save(account)
				).then(Mono.defer(() -> { // save profile then
					Map<String, String> content = new HashMap<>();
					content.put("firstName", profile.getFirstName());
					content.put("url", redirectUrl+"?token="+account.getToken());
					return mailService.sendEmail(account.getEmail(), "Registration", "register.html", content); // send registration email
				}));
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
