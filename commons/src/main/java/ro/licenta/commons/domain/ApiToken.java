package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ro.licenta.commons.components.GrantedAuthoritySerializer;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(ApiToken.API_TOKEN_KEYSPACE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ApiToken implements Serializable { 
	private static final long serialVersionUID = -7824933425552993053L;
	
	public static final String API_TOKEN_KEYSPACE = "api-token-keyspace";
	
	@Id
	private String id;
	private String key;
	@EqualsAndHashCode.Include
	private String user;
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private VapidSubscription vapidSubscription;
	@JsonDeserialize(using = GrantedAuthoritySerializer.class)
	private Set<GrantedAuthority> roles = new HashSet<>();

	@JsonIgnore
	public Boolean isValid() {
		return expiresAt == null || expiresAt.isAfter(LocalDateTime.now());
	}
	
	@JsonIgnore
	public Boolean hasRole(String role) {
		return roles.stream()
			.filter(r -> r.getAuthority().equals(role))
			.findAny().isPresent();
	}
	
	public ApiToken(Account account, Boolean rememberMe) {
		this.key = UUID.randomUUID().toString();
		this.user = account.getId();
		this.createdAt = LocalDateTime.now();
		this.expiresAt = this.createdAt.plusDays(1L);
		if (rememberMe) {
			this.expiresAt = this.createdAt.plusDays(30L);
		}
		account.getRoles().forEach(r -> {
			this.getRoles().add(new SimpleGrantedAuthority(r));
		});
	}
}
