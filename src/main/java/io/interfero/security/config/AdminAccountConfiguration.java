package io.interfero.security.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Getter(AccessLevel.PACKAGE)
@ConfigurationProperties("interfero.admin.account")
public class AdminAccountConfiguration
{
    private final String username;
    private final String password;

    AdminAccountConfiguration(@Nullable String username, @Nullable String password)
    {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Admin account password must be provided and cannot be blank.");

        this.username = username == null ? "admin" : username;
        this.password = password;
    }

    UserDetails toUserDetails()
    {
        return User.builder()
                .username(username)
                .password(password)
                .roles(Roles.ADMIN.name())
                .build();
    }
}
