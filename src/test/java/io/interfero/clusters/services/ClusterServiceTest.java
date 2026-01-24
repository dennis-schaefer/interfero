package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.clusters.repositories.ClusterRepository;
import org.apache.pulsar.client.admin.Clusters;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterServiceTest
{
    @Mock
    private ClusterRepository clusterRepository;

    @Mock
    private ClusterClientRegistry clusterClientRegistry;

    @Mock
    private PulsarAdmin pulsarAdmin;

    @Mock
    private Clusters pulsarClusters;

    @InjectMocks
    private ClusterService clusterService;

    @Test
    void shouldGetAllClusters() throws PulsarAdminException
    {
        var mockInternalName = "standalone";
        var mockCluster = new ClusterEntity("ABCD1234", "Test Cluster", "star", "#FF5733", 1L, 2L);
        var mockClusters = Set.of(mockCluster);
        when(clusterRepository.findAll()).thenReturn(mockClusters);
        when(clusterClientRegistry.getPulsarAdminForCluster(mockCluster.getId())).thenReturn(Optional.of(pulsarAdmin));
        when(pulsarAdmin.clusters()).thenReturn(pulsarClusters);
        when(pulsarClusters.getClusters()).thenReturn(List.of(mockInternalName));
        when(pulsarClusters.getCluster(mockInternalName)).thenReturn(ClusterData.builder().serviceUrl("http://localhost:8080").build());

        var clusters = clusterService.getAll();

        verify(clusterRepository).findAll();
        verify(clusterClientRegistry).getPulsarAdminForCluster(eq(mockCluster.getId()));
        verify(pulsarAdmin, atLeast(1)).clusters();
        verify(pulsarClusters).getClusters();
        verify(pulsarClusters).getCluster(mockInternalName);

        assertThat(clusters).isNotNull();
        assertThat(clusters).hasSize(1);
        var cluster = clusters.stream().toList().getFirst();
        shouldBeEqual(mockCluster, mockInternalName, cluster);
    }

    @Test
    void shouldGetNoClusters()
    {
        when(clusterRepository.findAll()).thenReturn(Set.of());

        var clusters = clusterService.getAll();

        verify(clusterRepository).findAll();
        assertThat(clusters).isNotNull();
        assertThat(clusters).isEmpty();
    }

    @Test
    void shouldGetClusterById() throws PulsarAdminException
    {
        var mockInternalName = "internal";
        var mockCluster = new ClusterEntity("1234ABCD", "Test Cluster", "circle", "#123456", 42L, 99L);
        when(clusterRepository.findById(mockCluster.getId())).thenReturn(Optional.of(mockCluster));
        when(clusterClientRegistry.getPulsarAdminForCluster(mockCluster.getId())).thenReturn(Optional.of(pulsarAdmin));
        when(pulsarAdmin.clusters()).thenReturn(pulsarClusters);
        when(pulsarClusters.getClusters()).thenReturn(List.of(mockInternalName));
        when(pulsarClusters.getCluster(mockInternalName)).thenReturn(ClusterData.builder().serviceUrl("http://localhost:8080").build());

        var clusterOpt = clusterService.getById(mockCluster.getId());

        verify(clusterRepository).findById(mockCluster.getId());
        verify(clusterClientRegistry).getPulsarAdminForCluster(eq(mockCluster.getId()));
        verify(pulsarAdmin, atLeast(1)).clusters();
        verify(pulsarClusters).getClusters();
        verify(pulsarClusters).getCluster(mockInternalName);
        assertThat(clusterOpt).isPresent();
        var cluster = clusterOpt.get();
        shouldBeEqual(mockCluster, mockInternalName, cluster);
    }

    @Test
    void shouldGetNoClusterById()
    {
        var clusterId = "9999ZZZZ";
        when(clusterRepository.findById(clusterId)).thenReturn(Optional.empty());

        var clusterOpt = clusterService.getById(clusterId);

        verify(clusterRepository).findById(clusterId);
        assertThat(clusterOpt).isNotPresent();
    }

    @Test
    void shouldCreateCluster() throws PulsarAdminException
    {
        var mockInternalName = "prod-cluster";
        var clusterToCreate = new ClusterEntity(null, "Production Cluster", "diamond", "#00FF00", 5L, 10L);
        when(clusterRepository.save(any(ClusterEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(clusterClientRegistry.getPulsarAdminForCluster(anyString())).thenReturn(Optional.of(pulsarAdmin));
        when(pulsarAdmin.clusters()).thenReturn(pulsarClusters);
        when(pulsarClusters.getClusters()).thenReturn(List.of(mockInternalName));
        when(pulsarClusters.getCluster(mockInternalName)).thenReturn(ClusterData.builder().serviceUrl("http://localhost:8080").build());

        var createdCluster = clusterService.create(clusterToCreate);

        verify(clusterRepository).save(any(ClusterEntity.class));
        verify(clusterClientRegistry).registerClientsForCluster(any(ClusterEntity.class));
        verify(clusterClientRegistry).getPulsarAdminForCluster(createdCluster.getId());
        verify(pulsarAdmin, atLeast(1)).clusters();
        verify(pulsarClusters).getClusters();
        verify(pulsarClusters).getCluster(mockInternalName);

        assertThat(createdCluster).isNotNull();
        shouldBeEqualExceptId(clusterToCreate, mockInternalName, createdCluster);
    }

    private void shouldBeEqual(ClusterEntity expected, String expectedInternalName, ClusterEntity actual)
    {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        shouldBeEqualExceptId(expected, expectedInternalName, actual);
    }

    private void shouldBeEqualExceptId(ClusterEntity expected, String expectedInternalName, ClusterEntity actual)
    {
        assertThat(actual.getDisplayName()).isEqualTo(expected.getDisplayName());
        assertThat(actual.getIcon()).isEqualTo(expected.getIcon());
        assertThat(actual.getColor()).isEqualTo(expected.getColor());
        assertThat(actual.getClientConnectionSettingsId()).isEqualTo(expected.getClientConnectionSettingsId());
        assertThat(actual.getAdminConnectionSettingsId()).isEqualTo(expected.getAdminConnectionSettingsId());
    }
}