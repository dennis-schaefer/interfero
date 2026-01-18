package io.interfero.clusters.services;

import io.interfero.TestcontainersConfiguration;
import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.domain.ClusterCreationEntity;
import io.interfero.clusters.repositories.ClusterConnectionSettingsRepository;
import io.interfero.clusters.repositories.ClusterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("it")
@Import(TestcontainersConfiguration.class)
class ClusterCreationServiceIT
{
    @Autowired
    private ClusterCreationService service;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private ClusterConnectionSettingsRepository clusterConnectionSettingsRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @AfterEach
    void tearDown()
    {
        clusterRepository.deleteAll();
        clusterConnectionSettingsRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateCluster()
    {
        var clientConnectionSettings = new ClusterConnectionSettingsEntity(null,
                TestcontainersConfiguration.pulsarClusterAContainer.getPulsarBrokerUrl(),
                ClusterAuthenticationMethod.NO_AUTH,
                null);
        var adminConnectionSettings = new ClusterConnectionSettingsEntity(null,
                TestcontainersConfiguration.pulsarClusterAContainer.getHttpServiceUrl(),
                ClusterAuthenticationMethod.NO_AUTH,
                null);
        var clusterToCreate = new ClusterCreationEntity(null, null, "Test Cluster", "star", "#FF5733",
                clientConnectionSettings, adminConnectionSettings);

        var createdCluster = service.createCluster(clusterToCreate);

        assertThat(createdCluster).isNotNull();
        assertThat(createdCluster.clusterId()).isNotNull();
        assertThat(createdCluster.internalName()).isNotNull();
        assertThat(createdCluster.displayName()).isEqualTo(clusterToCreate.displayName());
        assertThat(createdCluster.icon()).isEqualTo(clusterToCreate.icon());
        assertThat(createdCluster.color()).isEqualTo(clusterToCreate.color());
        assertThat(createdCluster.clientConnectionSettings()).isNotNull();
        assertThat(createdCluster.clientConnectionSettings().id()).isNotNull();
        assertThat(createdCluster.clientConnectionSettings().serviceUrl()).isEqualTo(clientConnectionSettings.serviceUrl());
        assertThat(createdCluster.clientConnectionSettings().authenticationMethod()).isEqualTo(clientConnectionSettings.authenticationMethod());
        assertThat(createdCluster.clientConnectionSettings().authenticationDetails()).isEqualTo(clientConnectionSettings.authenticationDetails());
        assertThat(createdCluster.adminConnectionSettings()).isNotNull();
        assertThat(createdCluster.adminConnectionSettings().id()).isNotNull();
        assertThat(createdCluster.adminConnectionSettings().serviceUrl()).isEqualTo(adminConnectionSettings.serviceUrl());
        assertThat(createdCluster.adminConnectionSettings().authenticationMethod()).isEqualTo(adminConnectionSettings.authenticationMethod());
        assertThat(createdCluster.adminConnectionSettings().authenticationDetails()).isEqualTo(adminConnectionSettings.authenticationDetails());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldRollbackClusterCreationOnFailure()
    {
        var clientConnectionSettings = new ClusterConnectionSettingsEntity(null,
                TestcontainersConfiguration.pulsarClusterAContainer.getHttpServiceUrl(),
                ClusterAuthenticationMethod.NO_AUTH,
                null);
        var adminConnectionSettings = new ClusterConnectionSettingsEntity(null,
                TestcontainersConfiguration.pulsarClusterAContainer.getHttpServiceUrl(),
                ClusterAuthenticationMethod.NO_AUTH,
                null);
        var clusterToCreate = new ClusterCreationEntity(null, null, null, "star", "#FF5733",
                clientConnectionSettings, adminConnectionSettings);

        assertThatThrownBy(() -> service.createCluster(clusterToCreate))
                .isInstanceOf(DataIntegrityViolationException.class);

        var allConnectionSettings = clusterConnectionSettingsRepository.findAll();
        assertThat(allConnectionSettings).isEmpty();

        var allClusters = clusterRepository.findAll();
        assertThat(allClusters).isEmpty();
    }
}