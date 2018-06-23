package com.herbinm.edx.captureme.gateway.authentication;

import com.amazonaws.regions.Regions;
import com.herbinm.edx.captureme.gateway.authentication.service.CognitoClientProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;

import static com.herbinm.edx.captureme.gateway.GatewayApplication.SessionAttribute.CURRENT_USER;

@Configuration
public class AuthenticationConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/myphotos");
    }

    @Bean
    public JWTProcessor jwtProcessor(CognitoClientProperties cognitoClientProperties) throws MalformedURLException {
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource keySource = new RemoteJWKSet(
                new URL(buildJWKSourceUrl(cognitoClientProperties)),
                new DefaultResourceRetriever(3600, 3600)
        );
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }

    @Bean
    public CognitoClientProperties cognitoClientProperties(
            @Value("${cognito.client.id}") String clientId,
            @Value("${cognito.domain}") String domain,
            @Value("${cognito.poolId}") String poolId,
            @Value("${cognito.client.secret}") String clientSecret
    ) {
        return new CognitoClientProperties(domain, poolId, clientId, clientSecret);
    }

    private String buildJWKSourceUrl(CognitoClientProperties cognitoClientProperties) {
        return String.format(
                "https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json",
                Regions.US_WEST_2.getName(),
                cognitoClientProperties.getPoolId()
        );
    }

    private class LoginInterceptor extends HandlerInterceptorAdapter {

        private final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            LOGGER.info("Checking if user is already logged into application");
            HttpSession session = request.getSession();
            if (session.getAttribute(CURRENT_USER) == null) {
                response.sendRedirect("/auth/login");
            }
            return session.getAttribute(CURRENT_USER) != null;
        }
    }

}
