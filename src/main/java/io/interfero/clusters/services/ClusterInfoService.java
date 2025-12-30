package io.interfero.clusters.services;

import io.interfero.clusters.PulsarClusterRegistry;
import io.interfero.clusters.domain.ClusterInfo;
import io.interfero.clusters.domain.ClusterInfoRecord;
import io.interfero.clusters.repositories.ClusterInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterInfoService
{
    private final ClusterInfoRepository clusterInfoRepository;
    private final PulsarClusterRegistry clusterRegistry;

    /**
     * Returns the cluster infos for all configured clusters. This also includes clusters that do not have any
     * additional info defined in the data storage yet.
     * @return A set of cluster infos.
     */
    public Set<ClusterInfo> getClusterInfos()
    {
        log.debug("Retrieving cluster infos for all configured clusters");
        var clusterNames = clusterRegistry.getConfiguredClusterNames();
        Set<ClusterInfo> clusterInfos = new HashSet<>();

        for (var clusterName : clusterNames)
            getClusterInfoByName(clusterName).ifPresent(clusterInfos::add);

        log.debug("Retrieved {} cluster infos", clusterInfos.size());
        clusterInfos.forEach(info -> log.trace(" - {}", info));
        return clusterInfos;
    }

    /**
     * Returns the cluster info for the given cluster name.
     * @param clusterName Configured cluster name
     * @return An optional containing the cluster info if found, otherwise an empty optional
     */
    public Optional<ClusterInfo> getClusterInfoByName(String clusterName)
    {
        log.debug("Retrieving cluster info for cluster: {}", clusterName);

        try
        {
            var internalName = getInternalClusterName(clusterName);
            var clusterInfoRecord = clusterInfoRepository.findByName(clusterName)
                    .orElse(new ClusterInfoRecord(clusterName));

            return Optional.of(ClusterInfo.from(clusterInfoRecord, internalName));
        }
        catch (Exception e)
        {
            log.error("Error retrieving cluster info for cluster '{}': {}", clusterName, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Determines the internal cluster name for the given configured cluster name.
     * @param clusterName The configured cluster name.
     * @return The internal cluster name.
     * @throws PulsarAdminException If an error occurs while retrieving cluster information.
     */
    public String getInternalClusterName(String clusterName) throws PulsarAdminException
    {
        log.debug("Determining internal cluster name for configured cluster: {}", clusterName);
        var pulsarAdmin = clusterRegistry.getPulsarAdmin(clusterName);

        var internalClusters = pulsarAdmin.clusters().getClusters();
        log.trace("Found {} internal clusters configured in cluster '{}': {}", internalClusters.size(),
                clusterName, internalClusters);

        for (var internalClusterName : internalClusters)
        {
            var clusterData = pulsarAdmin.clusters().getCluster(internalClusterName);
            log.trace("Service URL for '{}': {}", internalClusterName, clusterData.getServiceUrl());

            if (clusterData.getServiceUrl().contains("localhost"))
            {
                log.debug("Determined internal cluster name '{}' for configured cluster '{}' - matching 'localhost'", internalClusterName, clusterName);
                return internalClusterName;
            }
        }

        throw new PulsarAdminException("Could not determine internal cluster name for configured cluster: " + clusterName);
    }

    /**
     * Saves the given cluster info.
     * @param clusterInfo Cluster info to save
     * @return Saved cluster info
     * @throws IllegalArgumentException If the cluster name provided in the cluster info is not configured
     */
    public ClusterInfo saveClusterInfo(ClusterInfo clusterInfo)
    {
        log.info("Saving cluster info: {}", clusterInfo);
        if (!clusterRegistry.getConfiguredClusterNames().contains(clusterInfo.name()))
            throw new IllegalArgumentException("Cluster with name '" + clusterInfo.name() + "' is not configured");

        var record = ClusterInfoRecord.from(clusterInfo);

        var savedRecord = clusterInfoRepository.save(record);
        return ClusterInfo.from(savedRecord, clusterInfo.internalName());
    }
}
