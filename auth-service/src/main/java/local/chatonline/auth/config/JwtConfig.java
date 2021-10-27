package local.chatonline.auth.config;

import lombok.Data;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class JwtConfig {

    @Value("${security.jwt.uri:}")
    String uri;

    @Value("${security.jwt.header:}")
    String header;

    @Value("${security.jwt.prefix:}")
    String prefix;

    @Value("${security.jwt.expiration:1000}")
    int expiration;

    @Value("${security.jwt.secret:}")
    String secret;

}