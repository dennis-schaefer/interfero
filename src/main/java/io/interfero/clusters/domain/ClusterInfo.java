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

    public static ClusterInfo from(ClusterInfoRecord record, String internalName)
    {
        return new ClusterInfo(
                record.name(),
                internalName,
                record.displayName(),
                record.icon(),
                record.color()
        );
    }
}
