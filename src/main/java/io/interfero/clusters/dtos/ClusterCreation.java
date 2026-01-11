package io.interfero.clusters.dtos;

import jakarta.validation.Valid;

/**
 * DTO representing a cluster for the purpose of creation.
 * @param clusterInfo Information about the cluster
 * @param clientConnectionSettings Connection settings for Pulsar Client
 * @param adminConnectionSettings Connection settings for Pulsar Admin
 */
public record ClusterCreation(@Valid ClusterInfo clusterInfo,
                              @Valid ClusterConnectionSettings clientConnectionSettings,
                              @Valid ClusterConnectionSettings adminConnectionSettings)
{
}
