package io.interfero.security.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class BasicFilterChainConfigurationIT
{
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup()
    {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldAllowAccessToLoginPage() throws Exception
    {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAccessToLogout() throws Exception
    {
        mockMvc.perform(get("/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessToProtectedResource() throws Exception
    {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldAllowAccessToProtectedResourceWhenAuthenticated() throws Exception
    {
        mockMvc.perform(get("/api/hello").with(user("testuser")))
                .andExpect(status().isOk());
    }
}