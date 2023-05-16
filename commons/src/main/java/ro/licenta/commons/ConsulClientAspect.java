/**
 * 
 */
package ro.licenta.commons;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.context.annotation.Configuration;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * AOP fix to ensure the ACL token is sent when performing agent checks
 *
 * <p>https://github.com/spring-cloud/spring-cloud-consul/issues/559
 */
@Aspect
@Configuration
@RequiredArgsConstructor
@ConditionalOnConsulEnabled
@ConditionalOnProperty("spring.cloud.consul.discovery.acl-token")
@Log4j2
public class ConsulClientAspect {
    private final ConsulDiscoveryProperties consulDiscoveryProperties;

    @PostConstruct
    public void init() {
        log.warn("Hooking ConsulClient.agentCheckPass(String) calls to enforce acl token passing");
    }

    /**
     * Trap calls made to {@link ConsulClient#agentCheckPass(String)} and call the overridden method variant with ACL
     * token
     */
    @SneakyThrows
    @Around("execution (* com.ecwid.consul.v1.ConsulClient.agentCheckPass(String))")
    public Response<Void> trapAgentCheckPass(final ProceedingJoinPoint joinPoint) {
        final String checkId = (String) joinPoint.getArgs()[0];
        final ConsulClient client = (ConsulClient) joinPoint.getThis();

        return client.agentCheckPass(checkId, null, this.consulDiscoveryProperties.getAclToken());
    }
}