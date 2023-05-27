package ro.licenta.commons.service;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface MailService {

	public Mono<Void> sendEmail(String address, String subject, String templatePath, Map<String, String> content);
	
}
