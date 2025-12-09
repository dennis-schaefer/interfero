package io.interfero.security.controller;

import io.interfero.security.domain.AccountInfo;
import io.interfero.security.service.AccountInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/account-info")
@RequiredArgsConstructor
public class AccountInfoController
{
    private final AccountInfoService accountInfoService;

    /**
     * Get information about the authenticated user.
     * @param auth Authentication object provided by Spring Security, can be null if the user is not authenticated.
     * @return ResponseEntity containing AccountInfo if authenticated, or 401 Unauthorized if not.
     */
    @GetMapping
    ResponseEntity<AccountInfo> getAccountInfo(@Nullable Authentication auth)
    {
        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(auth);
        return accountInfo
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
