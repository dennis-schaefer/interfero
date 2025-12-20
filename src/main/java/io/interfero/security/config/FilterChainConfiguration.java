package io.interfero.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!it")
class FilterChainConfiguration
{
    @Bean
    @Order(3)
    @Profile("!dev")
    SecurityFilterChain prodApiSecurityFilterChain(HttpSecurity http, @Value("${server.port}") Integer serverPort)
    {
        http
                .securityMatcher(request -> request.getLocalPort() == serverPort &&
                        request.getServletPath().startsWith("/api"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll())
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    @Order(2)
    @Profile("!dev")
    SecurityFilterChain prodFrontendSecurityFilterChain(HttpSecurity http, @Value("${server.port}") Integer serverPort)
    {
        http
                .securityMatcher(request -> request.getLocalPort() == serverPort &&
                        !request.getServletPath().startsWith("/api"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((_request, response, _authentication) -> {
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": true}");
                        })
                        .failureHandler((_request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"error\": \"" + exception.getMessage() + "\"}");
                        })
                        .permitAll())
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    @Order(3)
    @Profile("dev")
    SecurityFilterChain devApiSecurityFilterChain(HttpSecurity http, @Value("${server.port}") Integer serverPort)
    {
        http
                .securityMatcher(request -> request.getLocalPort() == serverPort &&
                        request.getServletPath().startsWith("/api"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api-docs/v3").permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    @Order(2)
    @Profile("dev")
    SecurityFilterChain devFrontendSecurityFilterChain(HttpSecurity http, @Value("${server.port}") Integer serverPort,
                                                        DevAuthenticationSuccessHandler devAuthenticationSuccessHandler)
    {
        http
                .securityMatcher(request -> request.getLocalPort() == serverPort &&
                        !request.getServletPath().startsWith("/api"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/login").permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(devAuthenticationSuccessHandler)
                        .permitAll())
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http)
    {
        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
