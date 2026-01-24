package io.interfero.clusters.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class ClusterConnectionSettingsFileRepositoryIT extends ClusterConnectionSettingsRepositoryIT
{
    @Autowired
    private ClusterConnectionSettingsRepository clusterConnectionSettingsFileRepository;

    @BeforeEach
    void setUp()
    {
        this.clusterConnectionSettingsRepository = clusterConnectionSettingsFileRepository;
    }
}