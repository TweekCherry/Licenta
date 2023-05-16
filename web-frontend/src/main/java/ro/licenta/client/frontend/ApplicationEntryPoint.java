package ro.licenta.client.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import ro.licenta.commons.ConsulClientAspect;

@EnableDiscoveryClient
@Import(ConsulClientAspect.class)
@SpringBootApplication
public class ApplicationEntryPoint {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationEntryPoint.class, args);
	}

}
