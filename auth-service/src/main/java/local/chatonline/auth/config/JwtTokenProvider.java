package local.chatonline.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import lombok.AccessLevel;

import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JwtTokenProvider {

    final JwtConfig jwtConfig;

    public String generationToken(Authentication authentication) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authories", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000L))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8))
                .compact();

    }

    public Claims getClaimsFromJWT(String token) {

        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

    }

    public boolean validationToken(String authToken) {

        try {

            Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(authToken);

            return true;

        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;

    }
}