package io.interfero.clusters.services;

import io.interfero.clusters.PulsarClusterRegistry;
import io.interfero.clusters.repositories.ClusterInfoRepository;
import org.apache.pulsar.client.admin.Clusters;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.apache.pulsar.common.policies.data.ClusterDataImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterInfoServiceTest
{
    @Mock
    private PulsarClusterRegistry clusterRegistry;

    @Mock
    private PulsarAdmin pulsarAdmin;

    @Mock
    private Clusters clusters;

    @InjectMocks
    private ClusterInfoService service;

    @Test
    void shouldHaveMissingClusterInfo() throws PulsarAdminException
    {
        when(clusterRegistry.getPulsarAdmin(any(String.class))).thenReturn(pulsarAdmin);
        when(clusterRegistry.getConfiguredClusterNames()).thenReturn(Set.of("cluster-a", "cluster-b"));
        when(pulsarAdmin.clusters()).thenReturn(clusters);
        when(clusters.getClusters()).thenThrow(new PulsarAdminException("This is an expected test exception"));

        var clusterInfos = service.getClusterInfos();

        assertThat(clusterInfos).isNotNull();
        assertThat(clusterInfos).isEmpty();
    }

    @Test
    void shouldGetInternalClusterName() throws PulsarAdminException
    {
        when(clusterRegistry.getPulsarAdmin("cluster-a")).thenReturn(pulsarAdmin);
        when(pulsarAdmin.clusters()).thenReturn(clusters);
        when(clusters.getClusters()).thenReturn(List.of( "cluster-b", "standalone"));
        when(clusters.getCluster(eq("cluster-b")))
                .thenReturn(ClusterData.builder().serviceUrl("http://cluster-b:8080").build());
        when(clusters.getCluster(eq("standalone")))
                .thenReturn(ClusterData.builder().serviceUrl("http://localhost:8080").build());

        var internalName = service.getInternalClusterName("cluster-a");

        assertThat(internalName).isEqualTo("standalone");
    }

    @Test
    void shouldFailToGetInternalClusterNameWithMisingLocalhostServiceUrl() throws PulsarAdminException
    {
        when(clusterRegistry.getPulsarAdmin("cluster-a")).thenReturn(pulsarAdmin);
        when(pulsarAdmin.clusters()).thenReturn(clusters);
        when(clusters.getClusters()).thenReturn(List.of("cluster-b", "cluster-c"));
        when(clusters.getCluster(eq("cluster-b")))
                .thenReturn(ClusterData.builder().serviceUrl("http://cluster-b:8080").build());
        when(clusters.getCluster(eq("cluster-c")))
                .thenReturn(ClusterData.builder().serviceUrl("http://cluster-c:8080").build());

        assertThatThrownBy(() -> service.getInternalClusterName("cluster-a"))
                .isInstanceOf(PulsarAdminException.class)
                .hasMessage("Could not determine internal cluster name for configured cluster: cluster-a");
    }
}