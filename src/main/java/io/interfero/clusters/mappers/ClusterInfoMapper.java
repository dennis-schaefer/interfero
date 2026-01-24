package io.interfero.clusters.mappers;

import io.interfero.clusters.domain.ClusterCreationEntity;
import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.clusters.dtos.ClusterInfo;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting {@link ClusterEntity} and {@link ClusterCreationEntity} entities to {@link ClusterInfo} DTOs.
 */
@Component
public class ClusterInfoMapper
{
    /**
     * Converts a {@link ClusterEntity} to a {@link ClusterInfo} DTO.
     * @param cluster Cluster entity to convert
     * @return ClusterInfo DTO
     */
    public ClusterInfo toDto(ClusterEntity cluster)
    {
        return new ClusterInfo(cluster.getId(),
                cluster.getInternalName(),
                cluster.getDisplayName(),
                cluster.getIcon(),
                cluster.getColor());
    }

    /**
     * Converts a {@link ClusterCreationEntity} to a {@link ClusterInfo} DTO.
     * @param clusterCreation Cluster creation entity to convert
     * @return ClusterInfo DTO
     */
    public ClusterInfo toDto(ClusterCreationEntity clusterCreation)
    {
        return new ClusterInfo(clusterCreation.clusterId(),
                clusterCreation.internalName(),
                clusterCreation.displayName(),
                clusterCreation.icon(),
                clusterCreation.color());
    }
}
