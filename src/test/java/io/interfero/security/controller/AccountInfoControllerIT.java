package io.interfero.security.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class AccountInfoControllerIT
{
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldReturn401WithoutAuthentication() throws Exception
    {
        mockMvc.perform(get("/api/account-info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void shouldReturn401WithAnonymousAuthentication() throws Exception
    {
        mockMvc.perform(get("/api/account-info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", roles = {"ADMIN", "USER"})
    void shouldReturnAccountInfoForAuthenticatedUser() throws Exception
    {
        mockMvc.perform(get("/api/account-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-user"))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("ADMIN", "USER")));
    }

    @Test
    @WithMockUser(username = "simple-user", roles = {})
    void shouldReturnAccountInfoForAuthenticatedUserWithoutRoles() throws Exception
    {
        mockMvc.perform(get("/api/account-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("simple-user"))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles", hasSize(0)));
    }
}