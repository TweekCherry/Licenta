/**
 * 
 */
package ro.licenta.commons.service;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import ro.licenta.commons.config.security.CertificateContainer;

@Service
public class SslServiceImpl implements SslService {

	private final int strength = 4096;
	
	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		SslServiceImpl service = new SslServiceImpl();
		CertificateContainer rootCA = service.generate();
		String domain = "localhost.ro";
		List<String> alternativeDnsNames = new ArrayList<>();
		alternativeDnsNames.add("*."+domain);
		alternativeDnsNames.add(domain);
		List<String> alternativeIpAddresses = new ArrayList<>();
		alternativeIpAddresses.add("127.0.0.1");///loopback
		CertificateContainer serviceCertificate = service.generate(rootCA, "*."+domain, alternativeDnsNames, alternativeIpAddresses);
		System.out.println(":\r\n"
				+ "  ssl:\r\n"
				+ "    enabled: true\r\n"
				+ "    ca:");
		System.out.println(rootCA);
		System.out.println("    application:");
		System.out.println(serviceCertificate);
	}
	
	@Override
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

	@Override
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
		Date notBefore = new Date(System.currentTimeMillis() - Duration.ofDays(1L).toMillis());
		Date notAfter = new Date(System.currentTimeMillis() + Duration.ofDays(365L * 5).toMillis());
		X500Name subject = new X500NameBuilder()
			.addRDN(BCStyle.C, "ro")
			.addRDN(BCStyle.CN, subjectName)
			.addRDN(BCStyle.OU, "H&C")
			.addRDN(BCStyle.O, "Intelligent Health and Care")
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

	@Override
	@SneakyThrows
	public CertificateContainer generate() {
		X500Name issuer = new X500NameBuilder()
			.addRDN(BCStyle.C, "ro")
			.addRDN(BCStyle.CN, "localhost.ro")
			.addRDN(BCStyle.OU, "H&C")
			.addRDN(BCStyle.O, "Intelligent Health and Care")
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
		Date notBefore = new Date(System.currentTimeMillis() - Duration.ofDays(1L).toMillis());
		Date notAfter = new Date(System.currentTimeMillis() + Duration.ofDays(365L * 5).toMillis());
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
