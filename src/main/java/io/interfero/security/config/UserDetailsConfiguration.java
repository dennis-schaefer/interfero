package io.interfero.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Slf4j
@Configuration
@EnableConfigurationProperties(AdminAccountConfiguration.class)
public class UserDetailsConfiguration
{
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminAccountConfiguration adminAccountConfiguration)
    {
        var user = adminAccountConfiguration.toUserDetails();
        log.info("Configured admin account: '{}'", user);

        return new InMemoryUserDetailsManager(user);
    }
}
