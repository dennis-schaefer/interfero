package io.interfero.clusters.domain;

import org.jspecify.annotations.Nullable;

public record ClusterInfo(String name,
                          String internalName,
                          @Nullable String displayName,
                          @Nullable String icon,
                          @Nullable String color)
{
    /**
     * Indicates whether this cluster info has all attributes defined.
     * @return true if all attributes are defined, false otherwise
     */
    public boolean isFullyDefined()
    {
        return displayName != null && !displayName.isBlank() &&
                icon != null && !icon.isBlank() &&
                color != null && !color.isBlank();
    }
}
