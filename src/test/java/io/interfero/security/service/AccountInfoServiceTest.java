package io.interfero.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountInfoServiceTest
{
    private final AccountInfoService accountInfoService = new AccountInfoService();

    @Test
    void shouldReturnAccountInfoForAuthenticatedUser()
    {
        var authorities = List.<GrantedAuthority>of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER"));
        var user = new User("testuser", "password", authorities);
        var authentication = createAuthenticationMock(user);

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isPresent();
        assertThat(accountInfo.get().username()).isEqualTo("testuser");
        assertThat(accountInfo.get().roles()).containsOnly("ADMIN", "USER");
    }

    @Test
    void shouldReturnEmptyOptionalWhenAuthenticationIsNull()
    {
        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(null);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserIsNotAuthenticated()
    {
        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenPrincipalIsNull()
    {
        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(null);

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isEmpty();
    }

    @Test
    void shouldStripRolePrefixFromAuthorities()
    {
        var authorities = List.<GrantedAuthority>of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_VIEWER"));
        var user = new User("testuser", "password", authorities);
        var authentication = createAuthenticationMock(user);

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isPresent();
        assertThat(accountInfo.get().roles()).containsOnly("ADMIN", "USER", "VIEWER");
        assertThat(accountInfo.get().roles()).doesNotContain("ROLE_ADMIN", "ROLE_USER", "ROLE_VIEWER");
    }

    @Test
    void shouldHandleEmptyAuthorities()
    {
        var user = new User("testuser", "password", List.of());
        var authentication = createAuthenticationMock(user);

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isPresent();
        assertThat(accountInfo.get().username()).isEqualTo("testuser");
        assertThat(accountInfo.get().roles()).isEmpty();
    }

    @Test
    void shouldHandleUnsupportedPrincipalType()
    {
        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("unsupported-principal-type");

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isEmpty();
    }

    @Test
    void shouldFilterNullAuthorities()
    {
        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new TestGrantedAuthority(null),
                new SimpleGrantedAuthority("ROLE_USER"));
        var user = new User("testuser", "password", authorities);
        var authentication = createAuthenticationMock(user);

        var accountInfo = accountInfoService.getInfoForAuthenticatedUser(authentication);

        assertThat(accountInfo).isNotNull();
        assertThat(accountInfo).isPresent();
        assertThat(accountInfo.get().roles()).containsOnly("ADMIN", "USER");
        assertThat(accountInfo.get().roles()).hasSize(2);
    }

    private Authentication createAuthenticationMock(User user)
    {
        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        return authentication;
    }

    private record TestGrantedAuthority(String authority) implements GrantedAuthority
    {
        @Override
        public String getAuthority()
        {
            return authority;
        }
    }
}

