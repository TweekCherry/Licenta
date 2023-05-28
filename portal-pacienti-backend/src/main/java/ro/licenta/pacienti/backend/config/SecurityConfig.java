/**
 * 
 */
package ro.licenta.pacienti.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import ro.licenta.commons.config.security.DefaultSecurityConfig;
import ro.licenta.commons.domain.Roles;

@Configuration
@Import(DefaultSecurityConfig.class)
@EnableReactiveMethodSecurity(order = Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig {
	
	@Bean
	public SecurityWebFilterChain getSecurityWebFilterChain(@Qualifier(DefaultSecurityConfig.BEAN) ServerHttpSecurity http) {
		return http.authorizeExchange()
			.pathMatchers("/login").permitAll()
			.pathMatchers("/verify-token").permitAll()
			.pathMatchers("/register").permitAll()
			.pathMatchers("/forgot-password").permitAll()
			.anyExchange()
			.hasAnyRole(Roles.CLIENT_EXPLICIT)
        .and()
		.build();
	}
}
