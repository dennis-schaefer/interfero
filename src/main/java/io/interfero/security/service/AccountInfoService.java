package io.interfero.security.service;

import io.interfero.security.domain.AccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountInfoService
{
    /**
     * Extracts account information for the authenticated user from the given Authentication object.
     * If no authenticated user is found, an empty Optional is returned.
     * @param auth Authentication object representing the current user's authentication state.
     * @return An Optional containing UserInfo if an authenticated user is found, otherwise an empty Optional.
     */
    public Optional<AccountInfo> getInfoForAuthenticatedUser(@Nullable Authentication auth)
    {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null)
        {
            log.debug("No authenticated user found");
            return Optional.empty();
        }

        var userInfo = Optional.ofNullable(extractAccountInfoFromPrincipal(auth.getPrincipal()));
        log.debug("Authenticated user: {}", userInfo.orElse(null));
        return userInfo;
    }

    @Nullable
    private AccountInfo extractAccountInfoFromPrincipal(Object principal)
    {
        if (principal instanceof User user)
            return extractAccountInfoFromUser(user);

        log.warn("Unsupported principal type '{}' - This should really not happen", principal.getClass().getName());
        return null;
    }

    private AccountInfo extractAccountInfoFromUser(User user)
    {
        var username = user.getUsername();
        var roles = grantedAuthoritiesToRoles(user.getAuthorities());

        return new AccountInfo(username, roles);
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
