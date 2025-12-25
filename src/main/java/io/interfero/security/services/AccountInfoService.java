package io.interfero.security.services;

import io.interfero.security.domain.AccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountInfoService
{
    /**
     * Extracts account information from the given authentication object. If the authentication is null, not
     * authenticated, or has a no principal, an anonymous account info is returned.
     * @param auth The users authentication
     * @return The extracted account information
     */
    public AccountInfo getAccountInfoByAuthentication(@Nullable Authentication auth)
    {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null)
        {
            log.debug("No authenticated user found, returning anonymous account");
            return AccountInfo.anonymous();
        }

        return extractAccountInfoFromPrincipal(auth.getPrincipal());
    }

    private AccountInfo extractAccountInfoFromPrincipal(Object principal)
    {
        if (principal instanceof User user)
            return extractAccountInfoFromUser(user);

        log.error("Cannot extract account info from principal of type {} - This should really not happen", principal.getClass());
        return AccountInfo.anonymous();
    }

    private AccountInfo extractAccountInfoFromUser(User user)
    {
        var username = user.getUsername();
        var roles = grantedAuthoritiesToRoles(user.getAuthorities());

        return new AccountInfo(username, roles, true);
    }

    private Set<String> grantedAuthoritiesToRoles(Collection<? extends GrantedAuthority> authorities)
    {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toSet());
    }
}
