package io.interfero.clusters.mappers;

import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.dtos.ClusterConnectionSettings;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between {@link ClusterConnectionSettings} DTO and {@link ClusterConnectionSettingsEntity}.
 */
@Component
public class ClusterConnectionSettingsMapper
{
    /**
     * Converts a {@link ClusterConnectionSettings} DTO to a {@link ClusterConnectionSettingsEntity}.
     * @param dto DTO to convert
     * @return Converted entity
     */
    public ClusterConnectionSettingsEntity toEntity(ClusterConnectionSettings dto)
    {
        return new ClusterConnectionSettingsEntity(dto.id(), dto.serviceUrl(),
                dto.authenticationMethod(), dto.authenticationDetails());
    }

    /**
     * Converts a {@link ClusterConnectionSettingsEntity} to a {@link ClusterConnectionSettings} DTO.
     * @param entity Entity to convert
     * @return Converted DTO
     */
    public ClusterConnectionSettings toDto(ClusterConnectionSettingsEntity entity)
    {
        return new ClusterConnectionSettings(entity.id(), entity.serviceUrl(),
                entity.authenticationMethod(), entity.authenticationDetails());
    }
}
