package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.clusters.repositories.ClusterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterInitializerTest
{
    @Mock
    private ClusterRepository clusterRepository;

    @Mock
    private ClusterClientRegistry clientRegistry;

    @InjectMocks
    private ClusterInitializer clusterInitializer;

    @Test
    void shouldInitializeClientsForConfiguredClusters()
    {
        var cluster1 = new ClusterEntity("1", "Cluster 1", null, null, 1L, 2L);
        var cluster2 = new ClusterEntity("2", "Cluster 2", null, null, 3L, 4L);
        when(clusterRepository.findAll()).thenReturn(Set.of(cluster1, cluster2));

        clusterInitializer.initializeClients();

        verify(clientRegistry).registerClientsForCluster(cluster1);
        verify(clientRegistry).registerClientsForCluster(cluster2);
    }

    @Test
    void shouldHandleRegistrationFailureGracefully()
    {
        var cluster1 = new ClusterEntity("1", "Cluster 1", null, null, 1L, 2L);
        var cluster2 = new ClusterEntity("2", "Cluster 2", null, null, 3L, 4L);
        when(clusterRepository.findAll()).thenReturn(Set.of(cluster1, cluster2));
        doThrow(new RuntimeException("Failed to register")).when(clientRegistry).registerClientsForCluster(cluster1);

        clusterInitializer.initializeClients();

        verify(clientRegistry).registerClientsForCluster(cluster1);
        verify(clientRegistry).registerClientsForCluster(cluster2);
    }

    @Test
    void shouldDoNothingWhenNoClustersConfigured()
    {
        when(clusterRepository.findAll()).thenReturn(Set.of());

        clusterInitializer.initializeClients();

        verify(clientRegistry, never()).registerClientsForCluster(any());
    }
}
