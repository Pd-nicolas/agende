package com.agende.agendeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${security.jwt.token.secret:secret}")
    private String tokenSecret;

    public String getTokenSecret() {
        return tokenSecret;
    }
}
