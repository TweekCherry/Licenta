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
import org.springframework.scheduling.annotation.EnableScheduling;

import ro.licenta.commons.config.MongodbConfig;

@EnableScheduling
@EnableDiscoveryClient
@Import({ MongodbConfig.class })
@SpringBootApplication(scanBasePackages = {
	"ro.licenta.commons",
	"ro.licenta.pacienti.backend"
})
public class ApplicationEntryPoint {

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)));
		SpringApplication.run(ApplicationEntryPoint.class, args);
	}

}
