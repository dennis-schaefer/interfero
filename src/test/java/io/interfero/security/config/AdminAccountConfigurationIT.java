package io.interfero.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdminAccountConfigurationIT
{
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String ADMIN_USERNAME = "admin-test";
    private static final String ADMIN_PASSWORD = "admin-test-password";

    @Autowired
    private AdminAccountConfiguration adminAccountConfiguration;

    @Autowired
    private UserDetailsService userDetailsService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        registry.add("interfero.admin.account.username", () -> ADMIN_USERNAME);
        registry.add("interfero.admin.account.password", () ->
                Objects.requireNonNull(passwordEncoder.encode(ADMIN_PASSWORD)));
    }

    @Test
    void shouldHaveAdminAccountConfigured()
    {
        assertThat(adminAccountConfiguration.getUsername()).isEqualTo(ADMIN_USERNAME);
        assertThat(passwordEncoder.matches(ADMIN_PASSWORD, adminAccountConfiguration.getPassword())).isTrue();

        var userDetailsFromConfig = adminAccountConfiguration.toUserDetails();
        shouldBeAdminAccount(userDetailsFromConfig);

        var userDetailsFromService = userDetailsService.loadUserByUsername(ADMIN_USERNAME);
        shouldBeAdminAccount(userDetailsFromService);
    }

    private void shouldBeAdminAccount(UserDetails userDetails)
    {
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(ADMIN_USERNAME);
        assertThat(passwordEncoder.matches(ADMIN_PASSWORD, userDetails.getPassword())).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.getAuthorities().stream()
                .anyMatch(authority -> ("ROLE_" + Roles.ADMIN).equals(authority.getAuthority()))).isTrue();
    }
}