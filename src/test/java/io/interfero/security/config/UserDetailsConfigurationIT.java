package io.interfero.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class UserDetailsConfigurationIT
{
    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "$2a$12$Vsho9XvQ.BtnuU72PgRGN.R0ObbM7P4KRDfOlmWhAWiC2AN/NDqE6"; // secret

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsService userDetailsService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        registry.add("interfero.admin.account.username", () -> ADMIN_USERNAME);
        registry.add("interfero.admin.account.password", () -> ADMIN_PASSWORD);
    }

    @Test
    void shouldHaveBCryptPasswordEncoder()
    {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(passwordEncoder.matches("secret", ADMIN_PASSWORD)).isTrue();
    }

    @Test
    void shouldHaveAdminAccountInUserDetails()
    {
        var userDetails = userDetailsService.loadUserByUsername(ADMIN_USERNAME);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(ADMIN_USERNAME);
        assertThat(passwordEncoder.matches("secret", userDetails.getPassword())).isTrue();
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }
}