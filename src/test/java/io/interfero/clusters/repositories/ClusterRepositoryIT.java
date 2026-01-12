package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.domain.ClusterEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ClusterRepositoryIT
{
    protected ClusterRepository clusterRepository;
    protected ClusterConnectionSettingsRepository clusterConnectionSettingsRepository;

    @AfterEach
    void tearDown()
    {
        clusterRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindClusters()
    {
        var clientConnectionSettings = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:6550", ClusterAuthenticationMethod.NO_AUTH, "{}"));
        var adminConnectionSettings = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:8080", ClusterAuthenticationMethod.NO_AUTH, "{}"));

        assertThat(clientConnectionSettings.id()).isNotNull();
        assertThat(adminConnectionSettings.id()).isNotNull();

        var clusterToSave = new ClusterEntity("ABCD1234", "Test Cluster",  "star", "#FF0000",
                clientConnectionSettings.id(), adminConnectionSettings.id());

        var savedCluster = clusterRepository.save(clusterToSave);
        assertThat(savedCluster).isNotNull();
        assertThat(savedCluster).isEqualTo(clusterToSave);

        var allClusters = clusterRepository.findAll();
        assertThat(allClusters).isNotNull();
        assertThat(allClusters).contains(savedCluster);

        var clusterById = clusterRepository.findById(savedCluster.getId());
        assertThat(clusterById).isPresent();
        assertThat(clusterById.get()).isEqualTo(savedCluster);

        var clusterToUpdate = new ClusterEntity(savedCluster.getId(), "Updated Test Cluster", "circle", "#00FF00",
                clientConnectionSettings.id(), adminConnectionSettings.id());
        var updatedCluster = clusterRepository.save(clusterToUpdate);
        assertThat(updatedCluster).isNotNull();
        assertThat(updatedCluster).isEqualTo(clusterToUpdate);

        clusterById = clusterRepository.findById(updatedCluster.getId());
        assertThat(clusterById).isPresent();
        assertThat(clusterById.get()).isEqualTo(updatedCluster);
    }

    @Test
    void shouldNotFindNonExistingCluster()
    {
        var clusterById = clusterRepository.findById("99999999");
        assertThat(clusterById).isNotPresent();
    }

    @Test
    void shouldDeleteById()
    {
        var clientConnectionSettings = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:6550", ClusterAuthenticationMethod.NO_AUTH, "{}"));
        var adminConnectionSettings = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:8080", ClusterAuthenticationMethod.NO_AUTH, "{}"));

        assertThat(clientConnectionSettings.id()).isNotNull();
        assertThat(adminConnectionSettings.id()).isNotNull();

        var clusterToSave = new ClusterEntity("ABCD1234", "Test Cluster", "circle", "#FF0000",
                clientConnectionSettings.id(), adminConnectionSettings.id());

        var savedCluster = clusterRepository.save(clusterToSave);
        assertThat(savedCluster).isNotNull();

        clusterRepository.deleteById(savedCluster.getId());

        var clusterById = clusterRepository.findById(savedCluster.getId());
        assertThat(clusterById).isNotPresent();
    }

    @Test
    void shouldDeleteAllClusters()
    {
        var clientConnectionSettings1 = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:16550", ClusterAuthenticationMethod.NO_AUTH, "{}"));
        var adminConnectionSettings1 = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:18080", ClusterAuthenticationMethod.NO_AUTH, "{}"));
        var clientConnectionSettings2 = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:26550", ClusterAuthenticationMethod.NO_AUTH, "{}"));
        var adminConnectionSettings2 = clusterConnectionSettingsRepository.save(new ClusterConnectionSettingsEntity(null,
                "http://localhost:28080", ClusterAuthenticationMethod.NO_AUTH, "{}"));

        assertThat(clientConnectionSettings1.id()).isNotNull();
        assertThat(adminConnectionSettings1.id()).isNotNull();
        assertThat(clientConnectionSettings2.id()).isNotNull();
        assertThat(adminConnectionSettings2.id()).isNotNull();

        var cluster1 = new ClusterEntity("A0000001", "Cluster One", "circle", "#FF0000",
                clientConnectionSettings1.id(), adminConnectionSettings1.id());
        var cluster2 = new ClusterEntity("B0000002", "Cluster Two", "square", "#00FF00",
                clientConnectionSettings2.id(), adminConnectionSettings2.id());

        clusterRepository.save(cluster1);
        clusterRepository.save(cluster2);

        var allClusters = clusterRepository.findAll();
        assertThat(allClusters).hasSize(2);

        clusterRepository.deleteAll();

        allClusters = clusterRepository.findAll();
        assertThat(allClusters).isEmpty();
    }
}