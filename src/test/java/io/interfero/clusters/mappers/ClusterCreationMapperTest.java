package io.interfero.clusters.mappers;

import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.domain.ClusterCreationEntity;
import io.interfero.clusters.dtos.ClusterConnectionSettings;
import io.interfero.clusters.dtos.ClusterCreation;
import io.interfero.clusters.dtos.ClusterInfo;
import org.junit.jupiter.api.Test;

import static io.interfero.clusters.domain.ClusterAuthenticationMethod.NO_AUTH;
import static org.assertj.core.api.Assertions.assertThat;

class ClusterCreationMapperTest
{
    private final ClusterConnectionSettingsMapper connectionSettingsMapper = new ClusterConnectionSettingsMapper();
    private final ClusterInfoMapper clusterInfoMapper = new ClusterInfoMapper();
    private final ClusterCreationMapper clusterCreationMapper = new ClusterCreationMapper(connectionSettingsMapper, clusterInfoMapper);

    @Test
    void shouldMapToEntity()
    {
        var clusterInfo = new ClusterInfo("ABCD1234", "standalone", "Test Cluster", "star", "#FF5733");
        var clientConnectionSettings = new ClusterConnectionSettings(42L, "pulsar://localhost:6650", NO_AUTH, "{}");
        var adminConnectionSettings = new ClusterConnectionSettings(99L, "http://localhost:8080", NO_AUTH, "{}");
        var dto = new ClusterCreation(clusterInfo, clientConnectionSettings, adminConnectionSettings);

        var entity = clusterCreationMapper.toEntity(dto);
        assertThat(entity.clusterId()).isEqualTo(clusterInfo.clusterId());
        assertThat(entity.internalName()).isEqualTo(clusterInfo.internalName());
        assertThat(entity.displayName()).isEqualTo(clusterInfo.displayName());
        assertThat(entity.icon()).isEqualTo(clusterInfo.icon());
        assertThat(entity.color()).isEqualTo(clusterInfo.color());
        assertThat(entity.clientConnectionSettings().id()).isEqualTo(clientConnectionSettings.id());
        assertThat(entity.clientConnectionSettings().serviceUrl()).isEqualTo(clientConnectionSettings.serviceUrl());
        assertThat(entity.clientConnectionSettings().authenticationMethod()).isEqualTo(clientConnectionSettings.authenticationMethod());
        assertThat(entity.clientConnectionSettings().authenticationDetails()).isEqualTo(clientConnectionSettings.authenticationDetails());
        assertThat(entity.adminConnectionSettings().id()).isEqualTo(adminConnectionSettings.id());
        assertThat(entity.adminConnectionSettings().serviceUrl()).isEqualTo(adminConnectionSettings.serviceUrl());
        assertThat(entity.adminConnectionSettings().authenticationMethod()).isEqualTo(adminConnectionSettings.authenticationMethod());
        assertThat(entity.adminConnectionSettings().authenticationDetails()).isEqualTo(adminConnectionSettings.authenticationDetails());
    }

    @Test
    void shouldMapToDto()
    {
        var clientConnectionSettings = new ClusterConnectionSettingsEntity(42L, "pulsar://localhost:6650", NO_AUTH, "{}");
        var adminConnectionSettings = new ClusterConnectionSettingsEntity(99L, "http://localhost:8080", NO_AUTH, "{}");
        var entity = new ClusterCreationEntity("ABCD1234", "standalone", "Test Cluster", "star", "#FF5733", clientConnectionSettings, adminConnectionSettings);

        var dto = clusterCreationMapper.toDto(entity);

        assertThat(dto.clusterInfo().clusterId()).isEqualTo(entity.clusterId());
        assertThat(dto.clusterInfo().internalName()).isEqualTo(entity.internalName());
        assertThat(dto.clusterInfo().displayName()).isEqualTo(entity.displayName());
        assertThat(dto.clusterInfo().icon()).isEqualTo(entity.icon());
        assertThat(dto.clusterInfo().color()).isEqualTo(entity.color());
        assertThat(dto.clientConnectionSettings().id()).isEqualTo(entity.clientConnectionSettings().id());
        assertThat(dto.clientConnectionSettings().serviceUrl()).isEqualTo(entity.clientConnectionSettings().serviceUrl());
        assertThat(dto.clientConnectionSettings().authenticationMethod()).isEqualTo(entity.clientConnectionSettings().authenticationMethod());
        assertThat(dto.clientConnectionSettings().authenticationDetails()).isEqualTo(entity.clientConnectionSettings().authenticationDetails());
        assertThat(dto.adminConnectionSettings().id()).isEqualTo(entity.adminConnectionSettings().id());
        assertThat(dto.adminConnectionSettings().serviceUrl()).isEqualTo(entity.adminConnectionSettings().serviceUrl());
        assertThat(dto.adminConnectionSettings().authenticationMethod()).isEqualTo(entity.adminConnectionSettings().authenticationMethod());
        assertThat(dto.adminConnectionSettings().authenticationDetails()).isEqualTo(entity.adminConnectionSettings().authenticationDetails());
    }
}