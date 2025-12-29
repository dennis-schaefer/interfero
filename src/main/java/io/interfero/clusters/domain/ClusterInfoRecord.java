package io.interfero.clusters.domain;

import org.jspecify.annotations.Nullable;

public record ClusterInfoRecord(String name,
                                @Nullable String displayName,
                                @Nullable String icon,
                                @Nullable String color)
{
}
