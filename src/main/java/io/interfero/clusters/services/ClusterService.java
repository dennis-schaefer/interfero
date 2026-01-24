package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.clusters.repositories.ClusterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterService
{
    private static final int CLUSTER_ID_LENGTH = 8;
    private static final RandomStringGenerator ID_GENERATOR = RandomStringGenerator.builder()
            .withinRange('0', 'Z')
            .filteredBy(Character::isLetterOrDigit)
            .get();

    private final ClusterRepository clusterRepository;
    private final ClusterClientRegistry clusterClientRegistry;

    public Set<ClusterEntity> getAll()
    {
        log.debug("Fetching all Pulsar clusters from repository");
        var clusters = clusterRepository.findAll();

        log.debug("Found {} clusters in repository", clusters.size());
        clusters.forEach(cluster -> log.debug(" - {}", cluster));

        clusters.forEach(c -> c.setInternalName(getInternalClusterName(c.getId())));
        return clusters;
    }

    public Optional<ClusterEntity> getById(String clusterId)
    {
        log.debug("Retrieving cluster by id: {}", clusterId);
        var cluster = clusterRepository.findById(clusterId);

        log.debug("Found cluster for id '{}': {}", clusterId, cluster);
        cluster.ifPresent(c -> c.setInternalName(getInternalClusterName(c.getId())));

        return cluster;
    }

    ClusterEntity create(ClusterEntity cluster)
    {
        log.info("Creating new cluster: {}", cluster);
        var clusterToSave = new ClusterEntity(generateId(),
                cluster.getDisplayName(),
                cluster.getIcon(),
                cluster.getColor(),
                cluster.getClientConnectionSettingsId(),
                cluster.getAdminConnectionSettingsId());

        var savedCluster = clusterRepository.save(clusterToSave);
        clusterClientRegistry.registerClientsForCluster(savedCluster);

        savedCluster.setInternalName(getInternalClusterName(savedCluster.getId()));
        return savedCluster;
    }

    private String generateId()
    {
        var id = ID_GENERATOR.generate(CLUSTER_ID_LENGTH);

        if (clusterRepository.findById(id).isPresent()) // To ensure uniqueness
            return generateId();

        log.debug("Generating unique cluster id: {}", id);
        return id;
    }

    /**
     * Determines the internal cluster name for the given cluster.
     * @param clusterId The id of the cluster.
     * @return The internal cluster name.
     * @throws RuntimeException If an error occurs while retrieving cluster information.
     */
    public String getInternalClusterName(String clusterId)
    {
        log.debug("Determining internal cluster name for cluster with id '{}'", clusterId);
        var pulsarAdmin = clusterClientRegistry.getPulsarAdminForCluster(clusterId)
                .orElseThrow(() -> new RuntimeException("No Pulsar-Admin registered for cluster with id '" + clusterId + "'"));

        try
        {
            var internalClusters = pulsarAdmin.clusters().getClusters();
            log.trace("Found {} internal clusters in cluster with id '{}': {}", internalClusters.size(),
                    clusterId, internalClusters);

            for (var internalClusterName : internalClusters)
            {
                var clusterData = pulsarAdmin.clusters().getCluster(internalClusterName);
                log.trace("Service URL for '{}': {}", internalClusterName, clusterData.getServiceUrl());

                if (clusterData.getServiceUrl().contains("localhost"))
                {
                    log.debug("Determined internal cluster name '{}' for cluster with id '{}' - matching 'localhost'", internalClusterName, clusterId);
                    return internalClusterName;
                }
            }
        }
        catch (PulsarAdminException e)
        {
            log.error("Failed to load internal clusters from cluster with id '{}'", clusterId, e);
            throw new RuntimeException(e);
        }


        throw new RuntimeException("Could not determine internal cluster name for cluster with id '" + clusterId + "'");
    }
}
