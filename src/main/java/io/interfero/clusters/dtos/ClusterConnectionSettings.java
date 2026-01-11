package io.interfero.clusters.dtos;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * DTO for connection setting to a cluster (either Client or Admin).
 * @param id ID of the connection settings (or null on creation)
 * @param serviceUrl Service URL of the cluster (e.g., pulsar://pulsar.cluster.local:6650)
 * @param authenticationMethod Authentication method used for the connection
 * @param authenticationDetails Details required for the authentication method (e.g., token, username/password, etc.) -
 *                              can be null if no authentication ({@link ClusterAuthenticationMethod#NO_AUTH}) is
 *                              required
 */
public record ClusterConnectionSettings(@Nullable @jakarta.annotation.Nullable Long id,
                                        @NotNull @NotBlank String serviceUrl,
                                        @NotNull ClusterAuthenticationMethod authenticationMethod,
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
