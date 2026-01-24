package io.interfero.clusters.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * DTO representing cluster information.
 * @param clusterId ID of the cluster (or null on creation)
 * @param internalName Internal name of the cluster (or null on creation)
 * @param displayName Display name of the cluster
 * @param icon Icon representing the cluster
 * @param color Color representing the cluster in HEX format (e.g., #FF12AB)
 */
public record ClusterInfo(@Nullable @jakarta.annotation.Nullable String clusterId,
                          @Nullable @jakarta.annotation.Nullable String internalName,
                          @NotNull @NotBlank String displayName,
                          @NotNull @NotBlank String icon,
                          @NotNull @NotBlank @Pattern(regexp = "^#([A-Fa-f0-9]{6})$") String color)
{
    @Override
    public String toString()
    {
        return "ClusterInfo[" +
                "clusterId='" + clusterId + '\'' +
                ", internalName='" + internalName + '\'' +
                ", displayName='" + displayName + '\'' +
                ']';
    }
}
