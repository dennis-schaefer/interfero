package io.interfero.clusters.domain;

import org.jspecify.annotations.Nullable;

public record ClusterConnectionSettingsEntity(@Nullable Long id,
                                              String serviceUrl,
                                              ClusterAuthenticationMethod authenticationMethod,
                                              @Nullable String authenticationDetails)
{
    @Override
    public String toString()
    {
        return "ClusterConnectionSettings[" +
                "id=" + id +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", authenticationMethod=" + authenticationMethod +
                ']';
    }
}
