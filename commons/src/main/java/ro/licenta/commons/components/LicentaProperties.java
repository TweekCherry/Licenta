package ro.licenta.commons.components;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(LicentaProperties.PREFIX)
public class LicentaProperties {

	public static final String PREFIX = "licenta";
	
	private CertificateContainer ca = new CertificateContainer();
	private CertificateContainer application = new CertificateContainer();
	private PushService pushService = new PushService();
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class CertificateContainer {
		private String certificate;
		private String publicKey;
		private String privateKey;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class PushService {
		private String subject;
		private String publicKey;
		private String privateKey;
	}
	
}
