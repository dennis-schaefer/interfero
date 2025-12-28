package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfo;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing cluster info records in a persistent data store.
 */
public interface ClusterInfoRepository
{
    /**
     * Returns all cluster info records from the data store.
     * @return a set of all cluster info records
     */
    Set<ClusterInfo> findAll();

    /**
     * Returns a cluster info record by its name.
     * @param name the (configured) name of the cluster
     * @return an Optional containing the cluster info if found, or empty if not found
     */
    Optional<ClusterInfo> findByName(String name);

    /**
     * Saves a cluster info record to the data store. The name of the cluster info is used as the unique identifier.
     * @param clusterInfo the cluster info to save
     * @return the saved cluster info
     */
    ClusterInfo save(ClusterInfo clusterInfo);
}
