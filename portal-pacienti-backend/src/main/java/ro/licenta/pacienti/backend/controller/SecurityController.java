/**
 * 
 */
package ro.licenta.pacienti.backend.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
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
import ro.licenta.commons.repository.ProfileRepository;
import ro.licenta.commons.requests.BasicAuthenticationRequest;
import ro.licenta.commons.requests.ForgotPasswordRequest;
import ro.licenta.commons.requests.RegisterRequest;
import ro.licenta.commons.service.MailService;

@RestController
public class SecurityController {

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
			.switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials")));
	}
	
	@PostMapping("/forgot-password")
	public Mono<Void> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		return accountRepository.findByEmail(forgotPasswordRequest.getEmail())
			.flatMap(account -> accountRepository.save(account.setToken(UUID.randomUUID().toString())))
			.flatMap(account -> {
				Map<String, String> content = new HashMap<>();
				content.put("firstName", account.getEmail());
				content.put("url", "http://127.0.0.1:8000/recovery?token="+account.getToken());
				return mailService.sendEmail(account.getEmail(), "Password recovery", "/templates/recovery.html", content);
			});
	}
	
	@PostMapping("/register")
	public Mono<Void> register(@RequestBody RegisterRequest registerRequest) {
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
					profile.setDateOfBirth(registerRequest.getDateOfBirth());
					profile.setCnp(registerRequest.getCnp());
					profile.setGender(registerRequest.getGender());
					profile.setAge(registerRequest.getAge());
					profile.setAddress(new Address());
					profile.getAddress().setCity(registerRequest.getCity());
					profile.getAddress().setCounty(registerRequest.getCounty());
					profile.getAddress().setStreet(registerRequest.getStreet());
					profile.getAddress().setNumber(registerRequest.getNumber());
					profile.getAddress().setDetails(registerRequest.getDetails());
					return profileRepository.save(profile).then(Mono.defer(() -> { // save profile then
						Map<String, String> content = new HashMap<>();
						content.put("firstName", profile.getFirstName());
						content.put("url", "http://127.0.0.1:8000/register?token="+account.getToken());
						return mailService.sendEmail(a.getEmail(), "Registration", "/templates/register.html", content); // send registration email
					}));
				});
			});
	}
	
	@GetMapping("/verify-token")
	public Mono<Boolean> validateToken(@RequestParam("token") String token) {
		return accountRepository.existsByToken(token);
	}
	
	@PutMapping("/forgot-password")
	public Mono<String> completeForgotPassword(@RequestParam("token") String token, @RequestBody String password) {
		return accountRepository.findByToken(token)
			.flatMap(account -> {
				account.setToken(null);
				account.setPassword(passwordEncoder.encode(password));
				return accountRepository.save(account);
			}).map(Account::getId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid token")));
	}
	
	@PutMapping("/register")
	public Mono<String> completeRegister(@RequestParam("token") String token) {
		return accountRepository.findByToken(token)
			.flatMap(account -> {
				account.setToken(null);
				return accountRepository.save(account);
			}).map(Account::getId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid token")));
	}
}
