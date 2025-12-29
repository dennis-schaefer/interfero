package io.interfero.pulsar;

import io.interfero.TestcontainersConfiguration;
import io.interfero.clusters.PulsarClusterRegistry;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("it")
@Import(TestcontainersConfiguration.class)
public class PulsarClusterRegistryIT
{
    @Autowired
    private PulsarClusterRegistry pulsarClusterRegistry;

    private static final List<String> CONFIGURED_CLUSTERS = List.of("cluster-a", "cluster-b");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldReturnConfiguredClusterNames()
    {
        var clusterNames = pulsarClusterRegistry.getConfiguredClusterNames();
        assertThat(clusterNames).isNotNull();
        assertThat(clusterNames).containsExactlyInAnyOrder("cluster-a", "cluster-b");
    }


    @Test
    void shouldFindPulsarClients()
    {
        CONFIGURED_CLUSTERS.forEach(this::shouldFindPulsarClient);
    }

    private void shouldFindPulsarClient(String clusterName)
    {
        var client = pulsarClusterRegistry.getPulsarClient(clusterName);
        assertThat(client).isNotNull();
    }

    @Test
    void shouldNotFindInvalidClusterClient()
    {
        assertThatThrownBy(() -> pulsarClusterRegistry.getPulsarClient("non-existent-cluster"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pulsar cluster not found: non-existent-cluster");
    }

    @Test
    void shouldFindPulsarAdmins()
    {
        CONFIGURED_CLUSTERS.forEach(this::shouldFindPulsarAdmin);
    }

    private void shouldFindPulsarAdmin(String clusterName)
    {
        var admin = pulsarClusterRegistry.getPulsarAdmin(clusterName);
        assertThat(admin).isNotNull();
    }

    @Test
    void shouldNotFindInvalidClusterAdmin()
    {
        assertThatThrownBy(() -> pulsarClusterRegistry.getPulsarAdmin("non-existent-cluster"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pulsar cluster not found: non-existent-cluster");
    }

    @Test
    void shouldFindDefaultTenants()
    {
        CONFIGURED_CLUSTERS.forEach(this::shouldFindDefaultTenants);
    }

    private void shouldFindDefaultTenants(String clusterName)
    {
        try
        {
            var pulsarAdmin = pulsarClusterRegistry.getPulsarAdmin(clusterName);
            var tenants = pulsarAdmin.tenants().getTenants();
            assertThat(tenants).isNotNull();
            assertThat(tenants).contains("public", "pulsar");
        }
        catch (PulsarAdminException e)
        {
            e.printStackTrace();
            assertThat(true).isFalse(); // This should not happen
        }
    }
}
