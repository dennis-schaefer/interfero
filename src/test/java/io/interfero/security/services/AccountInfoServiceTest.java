package io.interfero.security.services;

import io.interfero.security.domain.AccountInfo;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AccountInfoServiceTest
{
    private final AccountInfoService service = new AccountInfoService();

    @Test
    void shouldReturnAnonymousForNullAuthentication()
    {
        var accountInfo = service.getAccountInfoByAuthentication(null);

        shouldBeAnonymous(accountInfo);
    }

    @Test
    void shouldReturnAnonymousForAuthenticationWithNullPrincipal()
    {
        var authentication = new UsernamePasswordAuthenticationToken(null, null);
        var accountInfo = service.getAccountInfoByAuthentication(authentication);

        shouldBeAnonymous(accountInfo);
    }

    @Test
    void shouldReturnAnonymousForUnauthenticatedAuthentication()
    {
        var authentication = new UsernamePasswordAuthenticationToken("user", "password");
        var accountInfo = service.getAccountInfoByAuthentication(authentication);

        shouldBeAnonymous(accountInfo);
    }

    @Test
    void shouldReturnAnonymousForUnsupportedPrincipalType()
    {
        var authentication = new UsernamePasswordAuthenticationToken("user", "password", Set.of());
        var accountInfo = service.getAccountInfoByAuthentication(authentication);

        shouldBeAnonymous(accountInfo);
    }

    @Test
    void shouldExtractAccountInfoFromUserPrincipal()
    {
        var user = User.builder()
                .username("test-user")
                .password("test-password")
                .roles("ADMIN", "USER")
                .build();
        var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        var accountInfo = service.getAccountInfoByAuthentication(authentication);
        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo.username()).isEqualTo("test-user");
        assertThat(accountInfo.roles()).containsExactlyInAnyOrder("ADMIN", "USER");
        assertThat(accountInfo.authenticated()).isTrue();
    }

    private void shouldBeAnonymous(AccountInfo accountInfo)
    {
        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo.username()).isEqualTo(AccountInfo.ANONYMOUS_USERNAME);
        assertThat(accountInfo.authenticated()).isFalse();
        assertThat(accountInfo.roles()).isEmpty();
        assertThat(accountInfo).isEqualTo(AccountInfo.anonymous());
    }
}