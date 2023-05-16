package ro.licenta.commons.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import ro.licenta.commons.BasicAuthenticationRequest;
import ro.licenta.commons.domain.Account;
import ro.licenta.commons.domain.ApiToken;
import ro.licenta.commons.repository.AccountRepository;
import ro.licenta.commons.repository.ApiTokenRepository;

@Log4j2
@Service
public class DefaultReactiveAutenticationServiceImpl implements ReactiveAutenticationService {

	@Autowired
	private ApiTokenRepository apiTokenRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Mono<Account> verifyCredentials(String username, String password) {
		return accountRepository.findByUsername(username)
			.filter(a -> passwordEncoder.matches(password, a.getPassword()));
	}
	
	@Override
	public Mono<ApiToken> verifyToken(String key) {
		return apiTokenRepository.findByKey(key)
			.filter(ApiToken::isValid);		
	}

	@Override
	public Mono<Void> clearApiToken(String key) {
		return apiTokenRepository.deleteByKey(key)
				.onErrorContinue((e, o) -> log.error(e.getMessage(), e));
	}

	@Override
	public Mono<ApiToken> authenticate(BasicAuthenticationRequest authenticationRequest) {
		return accountRepository.findByUsername(authenticationRequest.getUsername())
		.filter(account -> passwordEncoder.matches(authenticationRequest.getPassword(), account.getPassword()))
		.flatMap(this::processAuthentication)
		.switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials")));
	}

	private Mono<ApiToken> processAuthentication(Account account) {
		ApiToken apiToken = new ApiToken();
		apiToken.setKey(UUID.randomUUID().toString());
		apiToken.setCreatedAt(LocalDateTime.now());
		apiToken.setExpiresAt(apiToken.getCreatedAt().plusDays(30L));
		apiToken.setUser(account.getId());
		account.getRoles().forEach(r -> apiToken.getAuthorities().add(new SimpleGrantedAuthority(r)));
		return apiTokenRepository.save(apiToken);
	}

	@Override
	public Mono<Void> removeUserTokens(String userId) {
		return apiTokenRepository.deleteByUser(userId);
	}
}
