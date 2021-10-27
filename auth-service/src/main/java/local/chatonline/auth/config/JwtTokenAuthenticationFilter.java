package local.chatonline.auth.config;

import io.jsonwebtoken.Claims;

import local.chatonline.auth.model.CustomUser;

import local.chatonline.auth.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    final JwtConfig jwtConfig;
    final JwtTokenProvider jwtTokenProvider;
    final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(jwtConfig.getHeader());

        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(jwtConfig.getPrefix(), "");

        if (jwtTokenProvider.validationToken(token)) {

            Claims claims = jwtTokenProvider.getClaimsFromJWT(token);
            String username = claims.getSubject();

            UsernamePasswordAuthenticationToken auth = userService.findByUsername(username)
                    .map(CustomUser::new)
                    .map(user -> {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        return authenticationToken;
                    }).orElse(null);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);

    }
}