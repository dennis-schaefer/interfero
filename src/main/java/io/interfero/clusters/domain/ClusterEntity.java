package io.interfero.clusters.domain;

import lombok.*;
import org.jspecify.annotations.Nullable;

@Getter
@EqualsAndHashCode
@ToString(exclude = {"icon", "color", "clientConnectionSettingsId", "adminConnectionSettingsId"})
public class ClusterEntity
{
    private final String id;
    private final String displayName;
    private final String icon;
    private final String color;
    private final Long clientConnectionSettingsId;
    private final Long adminConnectionSettingsId;

    @Setter
    @Nullable
    private String internalName;

    public ClusterEntity(@Nullable String id, String displayName, String icon, String color,
                         Long clientConnectionSettingsId, Long adminConnectionSettingsId)
    {
        this.id = id == null ? "" : id;
        this.color = color;
        this.icon = icon;
        this.displayName = displayName;
        this.adminConnectionSettingsId = adminConnectionSettingsId;
        this.clientConnectionSettingsId = clientConnectionSettingsId;
    }
}
