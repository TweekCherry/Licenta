package ro.licenta.commons.service.impl;

import java.io.IOException;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import ro.licenta.commons.service.MailService;

@Log4j2
@Service
public class MailServiceImpl implements MailService {

	@Value("${spring.mail.username}")
	private String fromEmail;
	@Value("${server.address}")
	private String address;
	
    @Autowired(required = false)
    private JavaMailSender emailSender;
    
    @Autowired
    private Configuration freeMarkerConfigurationFactory;
    
	@Override
	public Mono<Void> sendEmail(String address, String subject, String templatePath, Map<String, String> content) {
		return Mono.fromSupplier(() -> {
			try {
	            MimeMessage mimeMessage = emailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
	            ModelMap modelMap = new ModelMap();
	            modelMap.mergeAttributes(content);
	            mimeMessage.setContent(this.build(templatePath, modelMap), "text/html");
	            helper.setTo(address);
	            helper.setSubject(subject);
	            helper.setFrom(fromEmail);
	            emailSender.send(mimeMessage);
	        } catch (Exception e) {
	            log.error(e.getMessage(), e);
	        }
			return null;
		});
	}

    private String build(String templatePath, ModelMap model) {
        String output = null;
        try {
            Template template = freeMarkerConfigurationFactory.getTemplate(templatePath);
            output = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (TemplateException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return output;
    }

}
