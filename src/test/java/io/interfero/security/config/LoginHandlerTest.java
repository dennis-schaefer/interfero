package io.interfero.security.config;

import io.interfero.security.domain.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import tools.jackson.databind.json.JsonMapper;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class LoginHandlerTest
{
    @Spy
    private JsonMapper jsonMapper = new JsonMapper();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginHandler loginHandler;

    @Test
    void shouldHandleSuccessfulAuthentication() throws Exception
    {
        // Given
        var stringWriter = new StringWriter();
        var writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // When
        loginHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        writer.flush();
        String jsonResponse = stringWriter.toString();
        LoginResponse loginResponse = jsonMapper.readValue(jsonResponse, LoginResponse.class);

        assertThat(loginResponse.success()).isTrue();
        assertThat(loginResponse.message()).isEqualTo("Login successful");
    }

    @Test
    void shouldHandleFailedAuthentication() throws Exception
    {
        // Given
        var errorMessage = "Bad credentials";
        var authException = mock(AuthenticationException.class);
        when(authException.getLocalizedMessage()).thenReturn(errorMessage);

        var stringWriter = new StringWriter();
        var writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // When
        loginHandler.onAuthenticationFailure(request, response, authException);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        writer.flush();
        String jsonResponse = stringWriter.toString();
        LoginResponse loginResponse = jsonMapper.readValue(jsonResponse, LoginResponse.class);

        assertThat(loginResponse.success()).isFalse();
        assertThat(loginResponse.message()).isEqualTo(errorMessage);
    }
}