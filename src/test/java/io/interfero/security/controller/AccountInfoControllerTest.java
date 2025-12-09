package io.interfero.security.controller;

import io.interfero.security.domain.AccountInfo;
import io.interfero.security.service.AccountInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountInfoControllerTest
{
    @Mock
    private AccountInfoService accountInfoService;

    @InjectMocks
    private AccountInfoController accountInfoController;

    @Test
    void shouldReturnAccountInfoWhenAuthenticated()
    {
        var authentication = mock(Authentication.class);
        var accountInfo = new AccountInfo("testuser", Set.of("ADMIN", "USER"));
        when(accountInfoService.getInfoForAuthenticatedUser(authentication))
                .thenReturn(Optional.of(accountInfo));

        var response = accountInfoController.getAccountInfo(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("testuser");
        assertThat(response.getBody().roles()).containsOnly("ADMIN", "USER");
        verify(accountInfoService).getInfoForAuthenticatedUser(authentication);
    }

    @Test
    void shouldReturn401WhenNotAuthenticated()
    {
        var authentication = mock(Authentication.class);
        when(accountInfoService.getInfoForAuthenticatedUser(authentication))
                .thenReturn(Optional.empty());

        var response = accountInfoController.getAccountInfo(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
        verify(accountInfoService).getInfoForAuthenticatedUser(authentication);
    }

    @Test
    void shouldReturnAccountInfoWithEmptyRoles()
    {
        var authentication = mock(Authentication.class);
        var accountInfo = new AccountInfo("testuser", Set.of());
        when(accountInfoService.getInfoForAuthenticatedUser(authentication))
                .thenReturn(Optional.of(accountInfo));

        var response = accountInfoController.getAccountInfo(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("testuser");
        assertThat(response.getBody().roles()).isEmpty();
    }
}

