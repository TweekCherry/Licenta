package ro.licenta.commons.config.security;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

class HotSwappableX509ExtendedKeyManager extends X509ExtendedKeyManager {

    private X509ExtendedKeyManager keyManager;

    public HotSwappableX509ExtendedKeyManager() {
    	
    }
    
    public HotSwappableX509ExtendedKeyManager(X509ExtendedKeyManager keyManager) {
        this.keyManager = Objects.requireNonNull(keyManager);
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return keyManager.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyTypes, Principal[] issuers, SSLEngine sslEngine) {
            return keyManager.chooseEngineClientAlias(keyTypes, issuers, sslEngine);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return keyManager.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine sslEngine) {
            return keyManager.chooseEngineServerAlias(keyType, issuers, sslEngine);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return keyManager.getPrivateKey(alias);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return keyManager.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return keyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return keyManager.getServerAliases(keyType, issuers);
    }

    public void setKeyManager(X509ExtendedKeyManager keyManager) {
        this.keyManager = Objects.requireNonNull(keyManager);
    }

}
