package io.interfero.clusters.mappers;

import io.interfero.clusters.domain.ClusterCreationEntity;
import io.interfero.clusters.dtos.ClusterCreation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between {@link ClusterCreationEntity} and {@link ClusterCreation} DTO.
 */
@Component
@RequiredArgsConstructor
public class ClusterCreationMapper
{
    private final ClusterConnectionSettingsMapper connectionSettingsMapper;
    private final ClusterInfoMapper clusterInfoMapper;

    /**
     * Converts a {@link ClusterCreation} DTO to a {@link ClusterCreationEntity}.
     * @param dto DTO to convert
     * @return Converted entity
     */
    public ClusterCreationEntity toEntity(ClusterCreation dto)
    {
        var clusterInfo = dto.clusterInfo();
        var clientConnectionSettings = connectionSettingsMapper.toEntity(dto.clientConnectionSettings());
        var adminConnectionSettings = connectionSettingsMapper.toEntity(dto.adminConnectionSettings());

        return new ClusterCreationEntity(clusterInfo.clusterId(),
                clusterInfo.internalName(),
                clusterInfo.displayName(),
                clusterInfo.icon(),
                clusterInfo.color(),
                clientConnectionSettings,
                adminConnectionSettings);
    }

    /**
     * Converts a {@link ClusterCreationEntity} to a {@link ClusterCreation} DTO.
     * @param entity Entity to convert
     * @return Converted DTO
     */
    public ClusterCreation toDto(ClusterCreationEntity entity)
    {
        var clusterInfo = clusterInfoMapper.toDto(entity);
        var clientConnectionSettings = connectionSettingsMapper.toDto(entity.clientConnectionSettings());
        var adminConnectionSettings = connectionSettingsMapper.toDto(entity.adminConnectionSettings());

        return new ClusterCreation(clusterInfo, clientConnectionSettings, adminConnectionSettings);
    }
}
