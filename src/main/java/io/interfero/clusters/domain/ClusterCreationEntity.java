package io.interfero.clusters.domain;

import org.jspecify.annotations.Nullable;

public record ClusterCreationEntity(@Nullable String clusterId,
                                    @Nullable String internalName,
                                    String displayName,
                                    String icon,
                                    String color,
                                    ClusterConnectionSettingsEntity clientConnectionSettings,
                                    ClusterConnectionSettingsEntity adminConnectionSettings)
{
    @Override
    public String toString()
    {
        return "ClusterCreationEntity[" +
                "clusterId='" + clusterId + '\'' +
                ", internalName='" + internalName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", clientConnectionSettings=" + clientConnectionSettings +
                ", adminConnectionSettings=" + adminConnectionSettings +
                ']';
    }
}
