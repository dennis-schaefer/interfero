package io.interfero.security.domain;

import java.util.Set;

public record AccountInfo(String username,
                          Set<String> roles,
                          boolean authenticated)
{
    public static final String ANONYMOUS_USERNAME = "anonymous";

    public static AccountInfo anonymous()
    {
        return new AccountInfo(ANONYMOUS_USERNAME, Set.of(), false);
    }
}
