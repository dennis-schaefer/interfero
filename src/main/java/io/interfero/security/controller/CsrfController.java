package io.interfero.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to generate a new CSRF token for the frontend.
 */
@Slf4j
@RestController
@RequestMapping("/api/csrf")
public class CsrfController
{
    @GetMapping
    @Operation(hidden = true)
    public void getCsrfToken(CsrfToken ignore)
    {
        // The CsrfToken argument and log message ensures that a CSRF token is generated
        log.trace("Generated CSRF token for header: {}", ignore.getHeaderName());
    }
}

