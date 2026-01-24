package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterCreationEntity;
import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.security.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterCreationService
{
    private final ClusterService clusterService;
    private final ClusterConnectionSettingsService connectionSettingsService;

    @Transactional
    @PreAuthorize("hasRole('" + Roles.ADMIN_ROLE + "')")
    public ClusterCreationEntity createCluster(ClusterCreationEntity clusterCreation)
    {
        var clientConnectionSettings = connectionSettingsService.create(clusterCreation.clientConnectionSettings());
        var adminConnectionSettings = connectionSettingsService.create(clusterCreation.adminConnectionSettings());

        assert clientConnectionSettings.id() != null;
        assert adminConnectionSettings.id() != null;

        var clusterToCreate = new ClusterEntity(null, clusterCreation.displayName(), clusterCreation.icon(),
                clusterCreation.color(), clientConnectionSettings.id(), adminConnectionSettings.id());
        var createdCluster = clusterService.create(clusterToCreate);

        return new ClusterCreationEntity(createdCluster.getId(),
                createdCluster.getInternalName(),
                createdCluster.getDisplayName(),
                createdCluster.getIcon(),
                clusterCreation.color(),
                clientConnectionSettings,
                adminConnectionSettings);
    }
}
