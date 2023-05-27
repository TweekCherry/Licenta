/**
 * 
 */
package ro.licenta.pacienti.backend;

import java.security.Security;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import ro.licenta.commons.ConsulClientAspect;
import ro.licenta.commons.config.MongodbConfig;
import ro.licenta.commons.config.security.DefaultSecurityConfig;

@EnableDiscoveryClient
@Import({MongodbConfig.class, ConsulClientAspect.class, DefaultSecurityConfig.class })
@SpringBootApplication(scanBasePackages = {
	"ro.licenta.commons",
	"ro.licenta.pacienti.backend.controller"
})
public class ApplicationEntryPoint {

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)));
		SpringApplication.run(ApplicationEntryPoint.class, args);
	}

}
