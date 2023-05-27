package ro.licenta.commons.config.security;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;

import lombok.extern.log4j.Log4j2;
import nl.martijndwars.webpush.PushService;
import reactor.core.publisher.Mono;
import ro.licenta.commons.components.LicentaProperties;
import ro.licenta.commons.domain.Roles;

@Log4j2
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@ComponentScan(basePackages = {
	"ro.licenta.commons.config.security"
})
@EnableConfigurationProperties(LicentaProperties.class)
public class DefaultSecurityConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
	
	public static final String BEAN = "serverHttpSecurity";
	
	@Autowired(required = false)
	private SslContextProvider contextHolder;
	
	@Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean(name = "pushService")
	@ConditionalOnProperty("licenta.push.service.publicKey")
	public PushService getPushService(
			@Value("${licenta.push.service.subject}") String subject, 
			@Value("${licenta.push.service.publicKey}") String publicKey, 
			@Value("${licenta.push.service.privateKey}") String privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		PushService pushService = new PushService();
		pushService.setPrivateKey(privateKey);
		pushService.setPublicKey(publicKey);
		pushService.setSubject(subject);
		return pushService;
	}
	
	@Primary
	@Bean(DefaultSecurityConfig.BEAN)
	public ServerHttpSecurity getServerHttpSecurity(CorsWebFilter webFiter, ServerHttpSecurity http, ReactiveAuthenticationManager reactiveAuthenticationManager, ServerSecurityContextRepository serverSecurityContextRepository, ServerLogoutSuccessHandler serverLogoutSuccessHandler) {
		SubjectDnX509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
	    principalExtractor.setSubjectDnRegex("CN=(.*?)(?:,|$)");
		http.addFilterAt(webFiter, SecurityWebFiltersOrder.CORS)
			.authorizeExchange()
				.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.pathMatchers(HttpMethod.POST, "/login").permitAll()
				.pathMatchers(HttpMethod.GET, "/verify-token").permitAll()
				.pathMatchers(HttpMethod.PUT, "/register").permitAll()
				.pathMatchers(HttpMethod.POST, "/register").permitAll()
				.pathMatchers(HttpMethod.PUT, "/forgot-password").permitAll()
				.pathMatchers(HttpMethod.POST, "/forgot-password").permitAll()
				.pathMatchers("/actuator/health").permitAll()
			.and()
				.logout()
					.logoutSuccessHandler(serverLogoutSuccessHandler)
			.and()
				.requestCache()
					.requestCache(NoOpServerRequestCache.getInstance())
			.and()
				.csrf().disable()
				.requestCache().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.headers().frameOptions().mode(Mode.SAMEORIGIN)
			.and()
				.securityContextRepository(serverSecurityContextRepository)
				.authenticationManager(reactiveAuthenticationManager)
                .exceptionHandling()
	                .authenticationEntryPoint((exchange, exception) -> {
	                	log.error("Exception occured for "+exchange.getRequest().getURI()+": "+exception.getMessage());
	                    ServerHttpResponse response = exchange.getResponse();
	                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
	                    response.getHeaders().set("Access-Control-Allow-Origin", "*");
                        response.getHeaders().set("www-authenticate", "YOLO GTFO");
	                    exchange.mutate().response(response);
	                    return Mono.empty();
	                })
	        .and()
	        .x509(x509 -> x509
				.authenticationManager(authentication -> {
					List<GrantedAuthority> authorities = new ArrayList<>();
					authorities.add(new SimpleGrantedAuthority(Roles.ROLE_ADMIN));
					authentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);
					return Mono.just(authentication);
				})
				.principalExtractor(principalExtractor)
			);
		return http;
	}
	
	@Bean
	public CorsRegistry corsRegistry() {
		CorsRegistry registry = new CorsRegistry();
		registry.addMapping("/**").allowedOrigins("*");
		return registry;
	}
	
	@Bean(name = "corsWebFilter")
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(CorsConfiguration.ALL));
		configuration.setAllowedMethods(Arrays.asList(CorsConfiguration.ALL));
		configuration.setAllowedHeaders(Arrays.asList(CorsConfiguration.ALL));
		configuration.setExposedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, "X-Auth-Token", "filename"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return new CorsWebFilter(source);
	}

	@Override
	public void customize(NettyReactiveWebServerFactory factory) {
		factory.addServerCustomizers(new SslServerCustomizer(contextHolder.getKeyManager(), contextHolder.getTrustManager()));
	}
}
