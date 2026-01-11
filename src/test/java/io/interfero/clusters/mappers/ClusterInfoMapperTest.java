package io.interfero.clusters.mappers;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.domain.ClusterEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterInfoMapperTest
{
    private final ClusterInfoMapper mapper = new ClusterInfoMapper();

    @Test
    void shouldMapClusterToDto()
    {
        var entity = new ClusterEntity("1234ABCD", "Test Cluster", "star", "#FF1122", 1L, 2L);

        var dto = mapper.toDto(entity);

        assertThat(dto.clusterId()).isEqualTo(entity.getId());
        assertThat(dto.internalName()).isEqualTo(entity.getInternalName());
        assertThat(dto.displayName()).isEqualTo(entity.getDisplayName());
        assertThat(dto.icon()).isEqualTo(entity.getIcon());
        assertThat(dto.color()).isEqualTo(entity.getColor());
    }

    @Test
    void shouldMapClusterCreationEntityToDto()
    {
        var clientConnectionSettings = new ClusterConnectionSettingsEntity(42L, "pulsar://localhost:6650", ClusterAuthenticationMethod.NO_AUTH, null);
        var adminConnectionSettings = new ClusterConnectionSettingsEntity(99L, "http://localhost:8080", ClusterAuthenticationMethod.NO_AUTH, null);
        var entity = new io.interfero.clusters.domain.ClusterCreationEntity(
                "5678EFGH",
                "standalone",
                "Test Cluster",
                "circle",
                "#00FF00",
                clientConnectionSettings,
                adminConnectionSettings
        );

        var dto = mapper.toDto(entity);

        assertThat(dto.clusterId()).isEqualTo(entity.clusterId());
        assertThat(dto.internalName()).isEqualTo(entity.internalName());
        assertThat(dto.displayName()).isEqualTo(entity.displayName());
        assertThat(dto.icon()).isEqualTo(entity.icon());
        assertThat(dto.color()).isEqualTo(entity.color());
    }
}