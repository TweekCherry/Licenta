package ro.licenta.commons.config.security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;

import io.netty.handler.ssl.ClientAuth;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

public class SslServerCustomizer implements NettyServerCustomizer {
	
	private final KeyManager keyManager;
	private final TrustManager trustManager;
	
	public SslServerCustomizer(KeyManager keyManager, TrustManager trustManager) {
		this.keyManager = keyManager;
		this.trustManager = trustManager;
	}
	
	@Override
	public HttpServer apply(HttpServer server) {
		return server.protocol(HttpProtocol.HTTP11, HttpProtocol.H2)
			.secure((spec) -> spec.sslContext(createSslContextSpec()));
	}
	
	protected Http2SslContextSpec createSslContextSpec() {
		return Http2SslContextSpec.forServer(keyManager).configure(builder -> {
			builder.trustManager(trustManager);
			builder.clientAuth(ClientAuth.OPTIONAL);
		});
	}
}
