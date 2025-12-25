package io.interfero.security.controller;

import io.interfero.security.domain.AccountInfo;
import io.interfero.security.services.AccountInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/account-info")
@RequiredArgsConstructor
public class AccountInfoController
{
    private final AccountInfoService accountInfoService;

    @GetMapping
    public ResponseEntity<AccountInfo> getAccountInfo(@Nullable Authentication auth)
    {
        var accountInfo = accountInfoService.getAccountInfoByAuthentication(auth);

        if (!accountInfo.authenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(accountInfo);
    }
}
