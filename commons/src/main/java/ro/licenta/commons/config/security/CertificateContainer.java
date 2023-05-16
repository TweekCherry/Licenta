package ro.licenta.commons.config.security;

import java.security.KeyFactory;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Container class for a x509 certificate.
 * Contains all the JCA and BC certificate data
 * */
@Getter
public final class CertificateContainer {

	private X509CertificateHolder certificateHolder;
	private RSAKeyParameters publicKeyParameters;
	private RSAPrivateCrtKeyParameters privateKeyParameters;
	
	private X509Certificate certificate;
	private RSAPrivateCrtKey privateKey;
	private RSAPublicKey publicKey;
	
	@SneakyThrows
	public CertificateContainer(X509CertificateHolder certificateHolder, RSAKeyParameters publicKeyParameters, RSAPrivateCrtKeyParameters privateKeyParameters) {
		this.certificateHolder = certificateHolder;
		this.publicKeyParameters = publicKeyParameters;
		this.privateKeyParameters = privateKeyParameters;
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		privateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(
            new RSAPrivateCrtKeySpec(
        		publicKeyParameters.getModulus(), 
        		publicKeyParameters.getExponent(),
        		privateKeyParameters.getExponent(), 
            	privateKeyParameters.getP(), 
            	privateKeyParameters.getQ(),
            	privateKeyParameters.getDP(), 
            	privateKeyParameters.getDQ(), 
            	privateKeyParameters.getQInv()
            )
        );
		publicKey = (RSAPublicKey) keyFactory.generatePublic(
			new RSAPublicKeySpec(
				publicKeyParameters.getModulus(), 
				publicKeyParameters.getExponent()
			)
		);
		certificate = new JcaX509CertificateConverter()
            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .getCertificate(certificateHolder);
	}

	@SneakyThrows
	public CertificateContainer(X509Certificate x509Certificate, RSAPublicKey rsaPublicKey, RSAPrivateCrtKey rsaPrivateKey) {
		this.publicKey = rsaPublicKey;
		this.privateKey = rsaPrivateKey;
		this.certificate = x509Certificate;
		this.publicKeyParameters = new RSAKeyParameters(false, publicKey.getModulus(), publicKey.getPublicExponent());
		this.privateKeyParameters = new RSAPrivateCrtKeyParameters(
			publicKey.getModulus(), 
			publicKey.getPublicExponent(), 
			privateKey.getPrivateExponent(),
			privateKey.getPrimeP(),
			privateKey.getPrimeQ(),
			privateKey.getPrimeExponentP(),
			privateKey.getPrimeExponentQ(),
			privateKey.getCrtCoefficient()
		);
		this.certificateHolder = new X509CertificateHolder(x509Certificate.getEncoded());
	}
	
	@Override
	public String toString() {
		try {// fk this but we need it
			return ""
			+ "      certificate: " + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "\n"
			+ "      privateKey: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
			+ "      publicKey: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + ""
			+ "";
		} catch (CertificateEncodingException e) {
			
		}
		return "";
	}
}
