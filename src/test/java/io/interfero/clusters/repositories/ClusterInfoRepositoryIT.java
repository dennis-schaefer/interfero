package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ClusterInfoRepositoryIT
{
    protected ClusterInfoRepository clusterInfoRepository;

    @Test
    void shouldSaveAndFindClusterInfo()
    {
        var clusterInfoToSave = new ClusterInfo(UUID.randomUUID().toString(), "standalone",
                "Test Cluster", "waves", "green");

        var savedClusterInfo = clusterInfoRepository.save(clusterInfoToSave);

        assertThat(savedClusterInfo).isNotNull();
        assertThat(savedClusterInfo).isEqualTo(clusterInfoToSave);

        var allClusterInfoRecords = clusterInfoRepository.findAll();
        assertThat(allClusterInfoRecords).isNotNull();
        assertThat(allClusterInfoRecords).contains(clusterInfoToSave);

        var clusterInfoByName = clusterInfoRepository.findByName(clusterInfoToSave.name());
        assertThat(clusterInfoByName).isPresent();
        assertThat(clusterInfoByName.get()).isEqualTo(clusterInfoToSave);
    }
}
