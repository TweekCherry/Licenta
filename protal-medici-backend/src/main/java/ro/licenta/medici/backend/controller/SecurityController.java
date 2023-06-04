/**
 * 
 */
package ro.licenta.medici.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.repository.AccountRepository;
import ro.licenta.commons.repository.ApiTokenRepository;
import ro.licenta.commons.requests.BasicAuthenticationRequest;
import ro.licenta.commons.requests.ForgotPasswordRequest;
import ro.licenta.commons.service.MailService;

@RestController
public class SecurityController {

	@Autowired
	private ApiTokenRepository apiTokenRepository;
	@Autowired
	private AccountRepository accountRepository;
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
}
