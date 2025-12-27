package io.interfero.security.config;

import io.interfero.security.domain.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler
{
    private final JsonMapper jsonMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        var loginResponse = new LoginResponse(true, "Login successful");
        writeResponse(response, HttpServletResponse.SC_OK, loginResponse);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException
    {
        var loginResponse = new LoginResponse(false, exception.getLocalizedMessage());
        writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, loginResponse);
    }

    private void writeResponse(HttpServletResponse response, int status, LoginResponse loginResponse) throws IOException
    {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(loginResponse));
        response.getWriter().flush();
    }
}
