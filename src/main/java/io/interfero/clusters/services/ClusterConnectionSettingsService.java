package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.dtos.ClusterConnectionSettings;
import io.interfero.clusters.repositories.ClusterConnectionSettingsRepository;
import io.interfero.security.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterConnectionSettingsService
{
    private final ClusterConnectionSettingsRepository connectionSettingsRepository;

    /**
     * Returns the cluster connection settings by the given id.
     * @param id The id of the connection settings
     * @return The connection settings if found, otherwise empty
     */
    public Optional<ClusterConnectionSettingsEntity> findById(Long id)
    {
        log.debug("Retrieving cluster connection settings by id [{}]", id);
        var connectionSettings = connectionSettingsRepository.findById(id);

        log.debug("Found following cluster connection settings for id [{}]: {}", id, connectionSettings);
        return connectionSettings;
    }

    /**
     * Creates a new connection setting for either Pulsar Client or Pulsar Admin for a cluster.
     * @param clusterConnectionSettings The connection settings to create
     * @return The created connection settings
     */
    ClusterConnectionSettingsEntity create(ClusterConnectionSettingsEntity clusterConnectionSettings)
    {
        log.info("Creating new cluster connection settings: {}" ,clusterConnectionSettings);
        var savedClusterConnectionSettings = connectionSettingsRepository.save(clusterConnectionSettings);

        log.debug("Created cluster connection settings successfully: {}", savedClusterConnectionSettings);
        return savedClusterConnectionSettings;
    }
}
