package io.interfero.clusters.mappers;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.dtos.ClusterConnectionSettings;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterConnectionSettingsMapperTest
{
    private final ClusterConnectionSettingsMapper mapper = new ClusterConnectionSettingsMapper();

    @Test
    void shouldMapDtoToEntity()
    {
        var dto = new ClusterConnectionSettings(42L, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{}");

        var entity = mapper.toEntity(dto);

        assertThat(entity.id()).isEqualTo(dto.id());
        assertThat(entity.serviceUrl()).isEqualTo(dto.serviceUrl());
        assertThat(entity.authenticationMethod()).isEqualTo(dto.authenticationMethod());
        assertThat(entity.authenticationDetails()).isEqualTo(dto.authenticationDetails());
    }

    @Test
    void shouldMapEntityToDto()
    {
        var entity = new ClusterConnectionSettingsEntity(999L, "https://my-pulsar.internal:6550",
                ClusterAuthenticationMethod.NO_AUTH, "{\"token\":\"1234567890\"}");

        var dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(entity.id());
        assertThat(dto.serviceUrl()).isEqualTo(entity.serviceUrl());
        assertThat(dto.authenticationMethod()).isEqualTo(entity.authenticationMethod());
        assertThat(dto.authenticationDetails()).isEqualTo(entity.authenticationDetails());
    }
}