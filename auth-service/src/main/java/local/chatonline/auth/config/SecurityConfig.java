package local.chatonline.auth.config;

import local.chatonline.auth.service.UserService;

import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.BeanIds;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.filter.CorsFilter;

import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    JwtConfig jwtConfig;

    @Autowired
    public void setJwtConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .and()
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtConfig, jwtTokenProvider, userService), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/signin").permitAll()
                .antMatchers(HttpMethod.POST, "/users").anonymous()
                .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsFilter corsFilter() {

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.addAllowedMethod("HEAD");
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("DELETE");
        corsConfiguration.addAllowedMethod("PATCH");

        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);

    }
}