package io.interfero.clusters.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
class ClusterInfoFileRepositoryIT extends ClusterInfoRepositoryIT
{
    @Autowired
    private ClusterInfoRepository clusterInfoFileRepository;

    @BeforeEach
    void setUp()
    {
        this.clusterInfoRepository = clusterInfoFileRepository;
    }
}