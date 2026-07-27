package com.agende.agendeapi.security;

import com.agende.agendeapi.config.AppProperties;
import com.agende.agendeapi.config.SpringApplicationContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static final long EXPIRATION_TIME = 3_600_000; // 1 hora
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");

        return appProperties.getTokenSecret();
    }

    public static String extractUserInfo(String token) {
        token = token.replace(TOKEN_PREFIX, "");

        String user = Jwts.parser()
                .setSigningKey(getTokenSecret())
                .parseClaimsJws( token )
                .getBody()
                .getSubject();

        return user;
    }

    public static Claims extractAuthorities(String token) {
        token = token.replace(TOKEN_PREFIX, "");

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(getTokenSecret())
                .parseClaimsJws(token);

        return claims.getBody();
    }

    public static String getCurrentUserNameSafe() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : "system";
        } catch (Exception e) {
            return "system";
        }
    }

    public static String getCurrentUserEmailSafe() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : "system@system";
        } catch (Exception e) {
            return "system@system";
        }
    }
}
