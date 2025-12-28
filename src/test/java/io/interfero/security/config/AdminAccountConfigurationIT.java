package io.interfero.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class AdminAccountConfigurationIT
{
    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "adminPassword";

    @Autowired
    private AdminAccountConfiguration adminAccountConfiguration;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        registry.add("interfero.admin.account.username", () -> ADMIN_USERNAME);
        registry.add("interfero.admin.account.password", () -> ADMIN_PASSWORD);
    }

    @Test
    void shouldInjectAdminAccountProperties()
    {
        assertThat(adminAccountConfiguration.getUsername()).isEqualTo(ADMIN_USERNAME);
        assertThat(adminAccountConfiguration.getPassword()).isEqualTo(ADMIN_PASSWORD);
    }
}