package com.herbinm.edx.captureme.gateway.authentication.service;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.herbinm.edx.captureme.gateway.domain.User;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;
import gherkin.deps.com.google.gson.JsonElement;
import gherkin.deps.com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Map;

import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@XRayEnabled
public class CognitoAuthentication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CognitoAuthentication.class);

    private final CognitoClientProperties cognitoClientProperties;
    private final String appBaseUrl;
    private final JWTProcessor jwtProcessor;
    private final JsonParser parser = new JsonParser();

    @Inject
    public CognitoAuthentication(CognitoClientProperties cognitoClientProperties,
                                 JWTProcessor jwtProcessor,
                                 @Value("${base.url}") String appDomain,
                                 @Value("${server.port}") String appPort) {
        this.cognitoClientProperties = cognitoClientProperties;
        this.jwtProcessor = jwtProcessor;
        this.appBaseUrl = "http://" + appDomain + ":" + appPort;
    }

    public String buildLoginUrl(String state) {
        return String.format(
                "https://%s/login?response_type=code&client_id=%s&state=%s&redirect_uri=%s/auth/callback"
                , cognitoClientProperties.getDomain(), cognitoClientProperties.getClientId(), state, appBaseUrl);
    }

    public User authenticate(String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("client_id", cognitoClientProperties.getClientId());
        requestParams.add("code", code);
        requestParams.add("redirect_uri", appBaseUrl + "/auth/callback");

        HttpEntity<Map> request = new HttpEntity<>(requestParams, createHeaders(
                cognitoClientProperties.getClientId(),
                cognitoClientProperties.getClientSecret()
        ));


        LOGGER.info("Exchanging code {} for token", code);
        ResponseEntity<String> responseEntity = restTemplate.exchange(buildOAuthUrl(), POST, request, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String idToken = getAttribute(responseEntity.getBody(), "id_token");
            try {
                JWTClaimsSet claimsSet = jwtProcessor.process(idToken, null);
                return new User(claimsSet.getStringClaim("nickname"), claimsSet.getStringClaim("cognito:username"));
            } catch (Exception ex) {
                LOGGER.error("Error decoding JWT certificate", ex);
                throw new RuntimeException(ex);
            }

        }

        return null;

    }

    private HttpHeaders createHeaders(String clientId, String clientSecret) {
        return new HttpHeaders() {{
            String auth = clientId + ":" + clientSecret;
            set("Authorization", "Basic " + new String(encodeBase64(auth.getBytes())));
            setContentType(APPLICATION_FORM_URLENCODED);
        }};
    }

    private String buildOAuthUrl() {
        return String.format("https://%s/oauth2/token", cognitoClientProperties.getDomain());
    }

    private String getAttribute(String json, String attribute) {
        JsonElement root = parser.parse(json);
        return root.getAsJsonObject().get(attribute).getAsString();
    }
}
