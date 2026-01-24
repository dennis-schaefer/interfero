package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.clusters.events.ClusterClientsRegisteredEvent;
import io.interfero.clusters.events.ClusterClientsUnregisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterClientRegistry
{
    private final ClusterConnectionSettingsService connectionSettingsService;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, PulsarClient> pulsarClients = new ConcurrentHashMap<>();
    private final Map<String, PulsarAdmin> pulsarAdmins = new ConcurrentHashMap<>();

    /**
     * Returns a Pulsar Admin for the given cluster id, if registered.
     * @param clusterId Cluster id
     * @return The registered Pulsar Admin, or empty if no client found for the given cluster id
     */
    Optional<PulsarAdmin> getPulsarAdminForCluster(String clusterId)
    {
        return Optional.ofNullable(pulsarAdmins.get(clusterId));
    }

    /**
     * Registers Pulsar Client and Admin for the given cluster. If there are already clients registered for the cluster,
     * they will be unregistered first. With the successful registration, a {@link ClusterClientsRegisteredEvent} will
     * be published containing the registered Pulsar Client and Admin.
     * @param cluster Cluster to register clients for
     * @throws IllegalArgumentException if the connection settings for the cluster are invalid
     * @throws RuntimeException if an error occurs while creating the clients
     */
    void registerClientsForCluster(ClusterEntity cluster)
    {
        unregisterClientsForCluster(cluster.getId());

        var clientConnectionSettings = connectionSettingsService.findById(cluster.getClientConnectionSettingsId())
                .orElseThrow(() -> new IllegalArgumentException("Client connection settings with id [" + cluster.getClientConnectionSettingsId() + "] not found"));
        var adminConnectionSettings = connectionSettingsService.findById(cluster.getAdminConnectionSettingsId())
                .orElseThrow(() -> new IllegalArgumentException("Admin connection settings with id [" + cluster.getAdminConnectionSettingsId() + "] not found"));
        try
        {
            var pulsarClient = createPulsarClientForConnectionSettings(clientConnectionSettings);
            pulsarClients.put(cluster.getId(), pulsarClient);

            var pulsarAdmin = createPulsarAdminForConnectionSettings(adminConnectionSettings);
            pulsarAdmins.put(cluster.getId(), pulsarAdmin);

            eventPublisher.publishEvent(new ClusterClientsRegisteredEvent(cluster.getId(), pulsarClient, pulsarAdmin));
        }
        catch (Exception e)
        {
            unregisterPulsarClientForCluster(cluster.getId());
            unregisterPulsarAdminForCluster(cluster.getId());
            throw new RuntimeException("Failed to create Pulsar Client and Admin for Cluster with id '" + cluster.getId() + "'", e);
        }
    }

    private void unregisterClientsForCluster(String clusterId)
    {
        unregisterPulsarClientForCluster(clusterId);
        unregisterPulsarAdminForCluster(clusterId);

        var event = new ClusterClientsUnregisteredEvent(clusterId);
        log.debug("Publishing: {}", event);
        eventPublisher.publishEvent(event);
    }

    private void unregisterPulsarClientForCluster(String clusterId)
    {
        log.debug("Unregistering Pulsar Client for cluster with id '{}'", clusterId);
        var pulsarClient = pulsarClients.remove(clusterId);
        if (pulsarClient == null)
            return;

        try {
            pulsarClient.close();
        } catch (PulsarClientException e) {
            log.error("Failed to close Pulsar Client for cluster id '{}'", clusterId, e);
        }
    }

    private void unregisterPulsarAdminForCluster(String clusterId)
    {
        log.debug("Unregistering Pulsar Admin for cluster with id '{}'", clusterId);
        var pulsarAdmin = pulsarAdmins.remove(clusterId);
        if (pulsarAdmin == null)
            return;

        try
        {
            pulsarAdmin.close();
        } catch (Exception e) {
            log.error("Failed to close Pulsar Admin for cluster id '{}'", clusterId, e);
        }
    }

    private PulsarClient createPulsarClientForConnectionSettings(ClusterConnectionSettingsEntity connectionSettings)
    {
        log.info("Creating Pulsar Client for: {}", connectionSettings);

        try
        {
            return PulsarClient.builder()
                    .serviceUrl(connectionSettings.serviceUrl())
                    .build();
        }
        catch (PulsarClientException e)
        {
            log.error("Failed to create Pulsar Client", e);
            throw new RuntimeException("Failed to create Pulsar Client for " + connectionSettings, e);
        }
    }

    private PulsarAdmin createPulsarAdminForConnectionSettings(ClusterConnectionSettingsEntity connectionSettings)
    {
        log.info("Creating Pulsar Admin for: {}", connectionSettings);

        try
        {
            return PulsarAdmin.builder()
                    .serviceHttpUrl(connectionSettings.serviceUrl())
                    .build();
        }
        catch (PulsarClientException e)
        {
            log.error("Failed to create Pulsar Admin", e);
            throw new RuntimeException("Failed to create Pulsar Admin for " + connectionSettings, e);
        }
    }
}
