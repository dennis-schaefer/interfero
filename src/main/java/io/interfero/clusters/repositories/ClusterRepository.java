package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterEntity;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing Pulsar clusters in a persistent data store.
 */
public interface ClusterRepository
{
    /**
     * Returns all configured clusters from the data store.
     * @return A set of all configured clusters
     */
    Set<ClusterEntity> findAll();

    /**
     * Returns a cluster by its unique identifier.
     * @param id The unique identifier of the cluster
     * @return An Optional containing the cluster if found, or empty if not found
     */
    Optional<ClusterEntity> findById(String id);

    /**
     * Saves a cluster to the data store. The ID of the cluster is used as the unique identifier.
     * @param cluster The cluster to save
     * @return The saved cluster
     */
    ClusterEntity save(ClusterEntity cluster);

    /**
     * Deletes a cluster by its unique identifier.
     * @param id The unique identifier of the cluster to delete
     */
    void deleteById(String id);

    /**
     * Deletes all clusters from the data store.
     */
    void deleteAll();
}
