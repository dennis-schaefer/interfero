package io.interfero.clusters.domain;

import org.jspecify.annotations.Nullable;

public record ClusterInfoRecord(String name,
                                @Nullable String displayName,
                                @Nullable String icon,
                                @Nullable String color)
{
    public ClusterInfoRecord(String name)
    {
        this(name, null, null, null);
    }

    public static ClusterInfoRecord from(ClusterInfo clusterInfo)
    {
        return new ClusterInfoRecord(
                clusterInfo.name(),
                clusterInfo.displayName(),
                clusterInfo.icon(),
                clusterInfo.color()
        );
    }
}
