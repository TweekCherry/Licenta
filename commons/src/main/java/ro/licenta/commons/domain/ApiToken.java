package ro.licenta.commons.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

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
	private Set<GrantedAuthority> authorities = new HashSet<>();

	@JsonIgnore
	public Boolean isValid() {
		return expiresAt == null || expiresAt.isAfter(LocalDateTime.now());
	}
	
	@JsonIgnore
	public Boolean hasRole(String role) {
		return authorities.stream()
			.filter(r -> r.getAuthority().equals(role))
			.findAny().isPresent();
	}
	
}
