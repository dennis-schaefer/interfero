package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfoRecord;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ClusterInfoRepositoryIT
{
    protected ClusterInfoRepository clusterInfoRepository;

    @Test
    void shouldSaveAndFindClusterInfo()
    {
        var clusterInfoToSave = new ClusterInfoRecord(UUID.randomUUID().toString(),
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

        var clusterInfoToUpdate = new ClusterInfoRecord(clusterInfoToSave.name(),
                "Updated Test Cluster", "waves", "blue");

        var updatedClusterInfo = clusterInfoRepository.save(clusterInfoToUpdate);
        assertThat(updatedClusterInfo).isNotNull();
        assertThat(updatedClusterInfo).isEqualTo(clusterInfoToUpdate);

        clusterInfoByName = clusterInfoRepository.findByName(updatedClusterInfo.name());
        assertThat(clusterInfoByName).isPresent();
        assertThat(clusterInfoByName.get()).isEqualTo(updatedClusterInfo);
    }

    @Test
    void shouldNotFindNonExistingClusterInfo()
    {
        var clusterInfoByName = clusterInfoRepository.findByName("non-existing-cluster");
        assertThat(clusterInfoByName).isNotPresent();
    }
}
