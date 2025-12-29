package io.interfero.pulsar;

import io.interfero.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("it")
@Import(TestcontainersConfiguration.class)
class PulsarConfigurationIT
{
    @Autowired
    private PulsarConfiguration pulsarConfiguration;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldHaveConfiguredClusters()
    {
        assertThat(pulsarConfiguration.getClusters()).hasSize(2);
        assertThat(pulsarConfiguration.getClusters()).containsOnlyKeys("cluster-a", "cluster-b");

        var clusterAProps = pulsarConfiguration.getClusters().get("cluster-a");
        assertThat(clusterAProps).isNotNull();
        assertThat(clusterAProps.getClient().getServiceUrl()).isNotBlank();
        assertThat(clusterAProps.getAdmin().getServiceUrl()).isNotBlank();

        var clusterBProps = pulsarConfiguration.getClusters().get("cluster-b");
        assertThat(clusterBProps).isNotNull();
        assertThat(clusterBProps.getClient().getServiceUrl()).isNotBlank();
        assertThat(clusterBProps.getAdmin().getServiceUrl()).isNotBlank();
    }
}