package io.interfero.security.config;


import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * This filter chain configuration enables basic form login.
 */
@Configuration
@EnableWebSecurity
class BasicFilterChainConfiguration
{
    @Bean
    @Order(1)
    SecurityFilterChain managementSecurityFilterChain(HttpSecurity http)
    {
        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http)
    {
       http
                .securityMatcher(req -> req.getRequestURI().startsWith("/api"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api-docs/v3").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/account-info").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/csrf").permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().fullyAuthenticated())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrfRequestHandler()));

        return http.build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain frontendSecurityFilterChain(HttpSecurity http, LoginHandler loginHandler)
    {
        http
                .securityMatcher(req -> !req.getRequestURI().startsWith("/api"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/assets/**").permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(loginHandler)
                        .failureHandler(loginHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutRequestMatcher(req -> req.getRequestURI().equals("/logout") && req.getMethod().equals("GET"))
                        .permitAll())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrfRequestHandler()));

        return http.build();
    }

    private CsrfTokenRequestAttributeHandler csrfRequestHandler()
    {
        var csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
        csrfRequestHandler.setCsrfRequestAttributeName("XSRF-TOKEN");
        return csrfRequestHandler;
    }
}
