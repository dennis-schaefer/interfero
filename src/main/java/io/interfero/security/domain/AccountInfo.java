package io.interfero.security.domain;

import java.util.Set;

public record AccountInfo(String username,
                          Set<String> roles)
{
}
