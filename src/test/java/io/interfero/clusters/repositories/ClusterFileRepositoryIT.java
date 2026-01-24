package io.interfero.clusters.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class ClusterFileRepositoryIT extends ClusterRepositoryIT
{
    @Autowired
    private ClusterRepository clusterFileRepository;

    @Autowired
    private ClusterConnectionSettingsRepository clusterConnectionSettingsRepositoryImpl;

    @BeforeEach
    void setUp()
    {
        this.clusterRepository = clusterFileRepository;
        this.clusterConnectionSettingsRepository = clusterConnectionSettingsRepositoryImpl;
    }
}