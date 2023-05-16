package ro.licenta.commons.config.security;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Component
@RefreshScope
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = "optilink.ssl.enabled", havingValue = "true")
public class SslContextProvider implements SslStoreProvider {
	
	private final int strength = 4096;
	
	@Autowired
	private SslConfigProperties sslConfigProperties;

	private KeyStore keyStore;
	private KeyStore trustStore;
	@Getter
	private SslContext clientSslContext;
	@Getter
	private CertificateContainer caCertificateContainer;
	@Getter
	private CertificateContainer serviceCertificateContainer;
	@Getter
	private HotSwappableX509ExtendedKeyManager keyManager;
	@Getter
	private HotSwappableX509ExtendedTrustManager trustManager;
	
	@SneakyThrows
	public SslContextProvider() {
		this.trustStore = KeyStore.getInstance("pkcs12");
		this.trustStore.load(null);
		this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		this.keyStore.load(null, new char[0]);
		this.keyManager = new HotSwappableX509ExtendedKeyManager();
		this.trustManager = new HotSwappableX509ExtendedTrustManager();
	}
	
	@PostConstruct
	public void postConstruct() throws Exception {
		log.info("Loading SSL context");
		this.caCertificateContainer = this.load(
			sslConfigProperties.getCa().getCertificate(), 
			sslConfigProperties.getCa().getPublicKey(), 
			sslConfigProperties.getCa().getPrivateKey()
		);
		this.serviceCertificateContainer = this.load(
			sslConfigProperties.getApplication().getCertificate(), 
			sslConfigProperties.getApplication().getPublicKey(), 
			sslConfigProperties.getApplication().getPrivateKey()
		);
		this.trustStore.setCertificateEntry("ca", caCertificateContainer.getCertificate());
		
		this.keyStore.setKeyEntry("vault", serviceCertificateContainer.getPrivateKey(), new char[0], new Certificate[] {
			serviceCertificateContainer.getCertificate(), caCertificateContainer.getCertificate()
		});
		
	    this.clientSslContext = SslContextBuilder.forClient()
			.trustManager(serviceCertificateContainer.getCertificate())
			.keyManager(serviceCertificateContainer.getPrivateKey(), serviceCertificateContainer.getCertificate())
			.build();
	    		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(this.trustStore);
		X509ExtendedTrustManager x509TrustManager = null;
		for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
		    if (tm instanceof X509ExtendedTrustManager) {
		        x509TrustManager = (X509ExtendedTrustManager) tm;
		        break;
		    }
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(this.keyStore, new char[0]);
		X509ExtendedKeyManager x509KeyManager = null;
		for (KeyManager km : keyManagerFactory.getKeyManagers()) {
			if (km instanceof X509ExtendedKeyManager) {
				x509KeyManager = (X509ExtendedKeyManager) km;
		        break;
		    }
		}
		keyManager.setKeyManager(x509KeyManager);
		trustManager.setTrustManager(x509TrustManager);
	    log.info("SSL context loaded");
	}
	
	@Override
	public KeyStore getKeyStore() throws Exception {
		return keyStore;
	}

	@Override
	public KeyStore getTrustStore() throws Exception {
		return trustStore;
	}
	
	/**
	 * Loads a certificate together with his public and private key. The strings must be DER Base64 encoded
	 * @param certificate - the DER encoded certificate using base64 string encoding
	 * @param publicKey - the public key in base64
	 * @param privateKey - the private key in base64 
	 * */
	@SneakyThrows
	public CertificateContainer load(String certificate, String publicKey, String privateKey) {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
		RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(privateKeySpec);
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		X509Certificate x509Certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(certificate)));
		return new CertificateContainer(x509Certificate, rsaPublicKey, rsaPrivateKey);
	}
	
	/**
	 * Generate an end user certificate based on the given CA certificate container
	 * @param rootCA - the CA certificate container used to issue end user certificates
	 * @param subjectName - the name of the subject for this certificate
	 * @param alternativeDnsNames - the list of alternative DNS names
	 * @param alternativeIpAddresses - the list of alternative IP addresses
	 * @return a certificate container for the end user
	 * @throws RuntimeException if anything goes wrong
	 * */
	@SneakyThrows
	public CertificateContainer generate(CertificateContainer rootCA, String subjectName, List<String> alternativeDnsNames, List<String> alternativeIpAddresses) {
		RSAKeyGenerationParameters parameters = new RSAKeyGenerationParameters(
			BigInteger.valueOf(0x10001), 
			CryptoServicesRegistrar.getSecureRandom(), 
			strength, 
			PrimeCertaintyCalculator.getDefaultCertainty(strength)
		);
		RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
		keyPairGenerator.init(parameters);
		AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
		BigInteger serialNumber = new BigInteger(20 * Byte.SIZE, new SecureRandom());
		Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		Date notAfter = new Date(System.currentTimeMillis() + 1 * 365 * 24 * 60 * 60 * 1000);
		X500Name subject = new X500NameBuilder()
			.addRDN(BCStyle.C, "NL")
			.addRDN(BCStyle.CN, subjectName)
			.addRDN(BCStyle.OU, "H&F")
			.addRDN(BCStyle.O, "Optilink")
			.build();
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(keyPair.getPublic());
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(rootCA.getCertificateHolder().getIssuer(), serialNumber, notBefore, notAfter, subject, subjectPublicKeyInfo);
		builder.addExtension(Extension.keyUsage, true, new X509KeyUsage(
			X509KeyUsage.digitalSignature | 
			X509KeyUsage.keyAgreement | 
			X509KeyUsage.dataEncipherment | 
			X509KeyUsage.keyEncipherment)
		);
		builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
		
		Stream<GeneralName> dnsNames = alternativeDnsNames.stream().map(s -> new GeneralName(GeneralName.dNSName, s));
		Stream<GeneralName> ipAddresses = alternativeIpAddresses.stream().map(s -> new GeneralName(GeneralName.iPAddress, s));
		GeneralName[] alternativeNames = Stream.concat(dnsNames, ipAddresses).toArray(GeneralName[]::new);
		builder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(alternativeNames));
		BcX509ExtensionUtils utils = new BcX509ExtensionUtils();
		builder.addExtension(Extension.subjectKeyIdentifier, false, utils.createSubjectKeyIdentifier(keyPair.getPublic()));
		builder.addExtension(Extension.authorityKeyIdentifier, false, utils.createAuthorityKeyIdentifier(rootCA.getPublicKeyParameters()));
		builder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(new KeyPurposeId[] {
			KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth
		}));
		
		ContentSigner contentSigner = new BcRSAContentSignerBuilder(
			new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE), 
			new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512)
		).build(rootCA.getPrivateKeyParameters());
		X509CertificateHolder certificateHolder = builder.build(contentSigner);
		return new CertificateContainer(certificateHolder, (RSAKeyParameters) keyPair.getPublic(), (RSAPrivateCrtKeyParameters) keyPair.getPrivate());
	}
	
	/**
	 * Generate a CA root certificate with validity of 10 years, this should be used to sign other certificates for end services
	 * @return a certificate container with for the CA
	 * @throws RuntimeException if anything fails
	 * */
	@SneakyThrows
	public CertificateContainer generate() {
		X500Name issuer = new X500NameBuilder()
			.addRDN(BCStyle.C, "NL")
			.addRDN(BCStyle.CN, "optilink.org")
			.addRDN(BCStyle.OU, "H&F")
			.addRDN(BCStyle.O, "Optilink")
			.build();
		RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
		keyPairGenerator.init(new RSAKeyGenerationParameters(
			BigInteger.valueOf(0x10001), 
			CryptoServicesRegistrar.getSecureRandom(), 
			strength, 
			PrimeCertaintyCalculator.getDefaultCertainty(strength)
		));
		AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
		BigInteger serialNumber = new BigInteger(20 * Byte.SIZE, new SecureRandom());
		Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		Date notAfter = new Date(System.currentTimeMillis() + 10 * 365 * 24 * 60 * 60 * 1000);
		SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(keyPair.getPublic());
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuer, serialNumber, notBefore, notAfter, issuer, subjectPublicKeyInfo);
		builder.addExtension(Extension.keyUsage, true, new X509KeyUsage(
			X509KeyUsage.cRLSign | 
			X509KeyUsage.keyCertSign)
		);
		builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
		BcX509ExtensionUtils utils = new BcX509ExtensionUtils();
		builder.addExtension(Extension.subjectKeyIdentifier, false, utils.createSubjectKeyIdentifier(keyPair.getPublic()));
		builder.addExtension(Extension.authorityKeyIdentifier, false, utils.createAuthorityKeyIdentifier(keyPair.getPublic()));
		ContentSigner contentSigner = new BcRSAContentSignerBuilder(
			new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE), 
			new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512)
		).build(keyPair.getPrivate());
		X509CertificateHolder certificateHolder = builder.build(contentSigner);
		return new CertificateContainer(certificateHolder, (RSAKeyParameters) keyPair.getPublic(), (RSAPrivateCrtKeyParameters) keyPair.getPrivate());
	}
}
