package ro.licenta.commons.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(SslConfigProperties.PREFIX)
public class SslConfigProperties {

	public static final String PREFIX = "optilink.ssl";
	/**
	 * Enable the SSL context and TLS authentication
	 * */
	private Boolean enabled = false;
	private CertificateContainer ca = new CertificateContainer();
	private CertificateContainer application = new CertificateContainer();
		
	@Getter
	@Setter
	@NoArgsConstructor
	public static class CertificateContainer {
		private String certificate;
		private String publicKey;
		private String privateKey;
	}
	
}
