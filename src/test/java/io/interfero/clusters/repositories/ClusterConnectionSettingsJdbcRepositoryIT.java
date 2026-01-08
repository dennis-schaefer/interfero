package io.interfero.clusters.repositories;

import io.interfero.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;


@SpringBootTest
@ActiveProfiles("it")
@Import(TestcontainersConfiguration.class)
class ClusterConnectionSettingsJdbcRepositoryIT extends ClusterConnectionSettingsRepositoryIT
{
    @Autowired
    private ClusterConnectionSettingsRepository clusterConnectionSettingsJdbcRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @BeforeEach
    void setUp()
    {
        this.clusterConnectionSettingsRepository = clusterConnectionSettingsJdbcRepository;
    }
}