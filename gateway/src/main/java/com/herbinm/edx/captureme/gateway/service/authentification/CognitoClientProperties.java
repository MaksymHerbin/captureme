package com.herbinm.edx.captureme.gateway.service.authentification;

public class CognitoClientProperties {

    private final String domain;
    private final String poolId;
    private final String clientId;
    private final String clientSecret;

    public CognitoClientProperties(String domain, String poolId, String clientId, String clientSecret) {
        this.domain = domain;
        this.poolId = poolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getDomain() {
        return domain;
    }

    public String getPoolId() {
        return poolId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
