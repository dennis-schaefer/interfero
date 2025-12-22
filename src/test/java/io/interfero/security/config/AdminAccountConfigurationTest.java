package io.interfero.security.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminAccountConfigurationTest
{
    @Test
    void shouldSetupAdminAccountWithDefaultUsername()
    {
        var adminAccountConfig = new AdminAccountConfiguration(null, "securePassword123");
        assertThat(adminAccountConfig.getUsername()).isEqualTo("admin");
        assertThat(adminAccountConfig.getPassword()).isEqualTo("securePassword123");
    }

    @Test
    void shouldThrowExceptionForBlankPassword()
    {
        assertThatThrownBy(() -> new AdminAccountConfiguration("adminUser", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Admin account password must be provided and cannot be blank.");
    }

    @Test
    void shouldConvertToUserDetails()
    {
        var adminAccountConfig = new AdminAccountConfiguration("adminUser", "securePassword123");
        var userDetails = adminAccountConfig.toUserDetails();

        assertThat(userDetails.getUsername()).isEqualTo("adminUser");
        assertThat(userDetails.getPassword()).isEqualTo("securePassword123");
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }
}