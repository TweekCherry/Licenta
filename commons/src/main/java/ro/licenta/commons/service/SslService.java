/**
 * 
 */
package ro.licenta.commons.service;

import java.util.List;

import ro.licenta.commons.config.security.CertificateContainer;

public interface SslService {
	
	/**
	 * Loads a certificate together with his public and private key. The strings must be DER Base64 encoded
	 * @param certificate - the DER encoded certificate using base64 string encoding
	 * @param publicKey - the public key in base64
	 * @param privateKey - the private key in base64 
	 * */
	public CertificateContainer load(String certificate, String publicKey, String privateKey);
	
	/**
	 * Generate an end user certificate based on the given CA certificate container
	 * @param rootCA - the CA certificate container used to issue end user certificates
	 * @param subjectName - the name of the subject for this certificate
	 * @param alternativeDnsNames - the list of alternative DNS names
	 * @param alternativeIpAddresses - the list of alternative IP addresses
	 * @return a certificate container for the end user
	 * @throws RuntimeException if anything goes wrong
	 * */
	public CertificateContainer generate(CertificateContainer rootCA, String subjectName, List<String> alternativeDnsNames, List<String> alternativeIpAddresses);
	
	/**
	 * Generate a CA root certificate with validity of 10 years, this should be used to sign other certificates for end services
	 * @return a certificate container with for the CA
	 * @throws RuntimeException if anything fails
	 * */
	public CertificateContainer generate();
}
