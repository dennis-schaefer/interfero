package io.interfero.clusters.services;

import io.interfero.TestcontainersConfiguration;
import io.interfero.clusters.domain.ClusterInfo;
import io.interfero.clusters.repositories.ClusterInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("it")
@Import(TestcontainersConfiguration.class)
class ClusterInfoServiceIT
{
    @Autowired
    private ClusterInfoService service;

    @Autowired
    private ClusterInfoRepository clusterInfoRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @AfterEach
    void tearDown()
    {
        clusterInfoRepository.deleteAll();
    }

    @Test
    void shouldHaveNoFullyDefinedClusterInfo()
    {
        var clusterInfos = service.getClusterInfos();

        assertThat(clusterInfos).isNotNull();
        assertThat(clusterInfos).hasSize(2);
        clusterInfos.forEach(info ->
        {
            assertThat(info.name()).isIn("cluster-a", "cluster-b");
            assertThat(info.internalName()).isEqualTo("standalone");
            assertThat(info.displayName()).isNull();
            assertThat(info.color()).isNull();
            assertThat(info.icon()).isNull();
            assertThat(info.isFullyDefined()).isFalse();
        });
    }

    @Test
    void shouldNotGetUnknownClusterInfo()
    {
        var clusterInfo = service.getClusterInfoByName("unknown-cluster");

        assertThat(clusterInfo).isNotPresent();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldSaveAndRetrieveClusterInfo()
    {
        var clusterInfoToSave = new ClusterInfo("cluster-a", "standalone",
                "Cluster A", "icon-a", "#FF0000");

        var savedClusterInfo = service.saveClusterInfo(clusterInfoToSave);

        assertThat(savedClusterInfo).isNotNull();
        assertThat(savedClusterInfo).isEqualTo(clusterInfoToSave);

        var clusterInfos = service.getClusterInfos();
        assertThat(clusterInfos).isNotNull();
        assertThat(clusterInfos).contains(savedClusterInfo);

        var retrievedClusterInfo = service.getClusterInfoByName("cluster-a");
        assertThat(retrievedClusterInfo).isPresent();
        assertThat(retrievedClusterInfo.get()).isEqualTo(savedClusterInfo);


        var clusterInfoToUpdate = new ClusterInfo("cluster-a", "standalone",
                "Cluster A Updated", "icon-a-updated", "#00FF00");

        var updatedClusterInfo = service.saveClusterInfo(clusterInfoToUpdate);

        assertThat(updatedClusterInfo).isNotNull();
        assertThat(updatedClusterInfo).isEqualTo(clusterInfoToUpdate);

        retrievedClusterInfo = service.getClusterInfoByName("cluster-a");
        assertThat(retrievedClusterInfo).isPresent();
        assertThat(retrievedClusterInfo.get()).isEqualTo(updatedClusterInfo);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldFailToSaveClusterInfoWithUnknownClusterName()
    {
        var clusterInfo = new ClusterInfo("unknown-cluster", "standalone",
                "Unknown Cluster", "icon-unknown", "#FFFFFF");

        assertThatThrownBy(() -> service.saveClusterInfo(clusterInfo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cluster with name 'unknown-cluster' is not configured");
    }
}