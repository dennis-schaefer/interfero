package io.interfero.clusters;

import io.interfero.TestcontainersConfiguration;
import io.interfero.clusters.domain.*;
import io.interfero.clusters.dtos.ClusterConnectionSettings;
import io.interfero.clusters.dtos.ClusterCreation;
import io.interfero.clusters.dtos.ClusterInfo;
import io.interfero.clusters.repositories.ClusterConnectionSettingsRepository;
import io.interfero.clusters.repositories.ClusterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.pulsar.PulsarContainer;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("it")
class ClustersIT
{
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private ClusterConnectionSettingsRepository clusterConnectionSettingsRepository;

    private final JsonMapper jsonMapper = new JsonMapper();
    private MockMvc mockMvc;

    @BeforeEach
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void setUp()
    {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown()
    {
        clusterRepository.deleteAll();
        clusterConnectionSettingsRepository.deleteAll();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldCreateAndRetrieveClusters() throws Exception
    {
        shouldCreateCluster(TestcontainersConfiguration.pulsarClusterAContainer, "Pulsar Cluster A");
        shouldCreateCluster(TestcontainersConfiguration.pulsarClusterBContainer, "Pulsar Cluster B");

        // Retrieve all clusters
        var result = mockMvc.perform(get("/api/clusters")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        Set<ClusterInfo> clusters = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(clusters).hasSize(2);

        var clusterA = clusters.stream()
                .filter(c -> c.displayName().equals("Pulsar Cluster A"))
                .findFirst();
        assertThat(clusterA).isPresent();
        assertThat(clusterA.get().clusterId()).isNotNull();
        assertThat(clusterA.get().internalName()).isNotNull();
        assertThat(clusterA.get().icon()).isEqualTo("star");
        assertThat(clusterA.get().color()).isEqualTo("#FFFFFF");

        var clusterB = clusters.stream()
                .filter(c -> c.displayName().equals("Pulsar Cluster B"))
                .findFirst();
        assertThat(clusterB).isPresent();
        assertThat(clusterB.get().clusterId()).isNotNull();
        assertThat(clusterB.get().internalName()).isNotNull();
        assertThat(clusterB.get().icon()).isEqualTo("star");
        assertThat(clusterB.get().color()).isEqualTo("#FFFFFF");


        // Retrieve cluster by id
        mockMvc.perform(get("/api/clusters/" + clusterA.get().clusterId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clusterId").value(clusterA.get().clusterId()))
                .andExpect(jsonPath("$.internalName").value(clusterA.get().internalName()))
                .andExpect(jsonPath("$.displayName").value(clusterA.get().displayName()))
                .andExpect(jsonPath("$.icon").value(clusterA.get().icon()))
                .andExpect(jsonPath("$.color").value(clusterA.get().color()));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldFailToCreateClusterWithoutAdminRole() throws Exception
    {
        var clusterToCreate = getValidClusterToCreate(TestcontainersConfiguration.pulsarClusterAContainer, "Pulsar Cluster A");
        var clusterToCreateJson = jsonMapper.writeValueAsString(clusterToCreate);

        mockMvc.perform(post("/api/clusters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(clusterToCreateJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldFailToCreateInvalidCluster() throws Exception
    {
        var clusterToCreate = getValidClusterToCreate(TestcontainersConfiguration.pulsarClusterAContainer, "Pulsar Cluster A");

        // Invalidate the cluster by removing the display name
        var invalidClusterInfo = new io.interfero.clusters.dtos.ClusterInfo(null, null,
                "", clusterToCreate.clusterInfo().icon(), clusterToCreate.clusterInfo().color());
        var invalidClusterToCreate = new ClusterCreation(invalidClusterInfo,
                clusterToCreate.clientConnectionSettings(),
                clusterToCreate.adminConnectionSettings());
        var clusterToCreateJson = jsonMapper.writeValueAsString(invalidClusterToCreate);

        mockMvc.perform(post("/api/clusters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(clusterToCreateJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    private void shouldCreateCluster(PulsarContainer pulsarContainer, String displayName) throws Exception
    {
        var clusterToCreate = getValidClusterToCreate(pulsarContainer, displayName);
        var clusterToCreateJson = jsonMapper.writeValueAsString(clusterToCreate);

        var result = mockMvc.perform(post("/api/clusters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("admin").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(clusterToCreateJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clusterInfo.clusterId").isNotEmpty())
                .andExpect(jsonPath("$.clusterInfo.internalName").isNotEmpty())
                .andExpect(jsonPath("$.clusterInfo.displayName").value(clusterToCreate.clusterInfo().displayName()))
                .andExpect(jsonPath("$.clusterInfo.icon").value(clusterToCreate.clusterInfo().icon()))
                .andExpect(jsonPath("$.clusterInfo.color").value(clusterToCreate.clusterInfo().color()))
                .andExpect(jsonPath("$.clientConnectionSettings.id").isNotEmpty())
                .andExpect(jsonPath("$.clientConnectionSettings.serviceUrl").value(clusterToCreate.clientConnectionSettings().serviceUrl()))
                .andExpect(jsonPath("$.clientConnectionSettings.authenticationMethod", is(clusterToCreate.clientConnectionSettings().authenticationMethod().name())))
                .andExpect(jsonPath("$.clientConnectionSettings.authenticationDetails").value(clusterToCreate.clientConnectionSettings().authenticationDetails()))
                .andExpect(jsonPath("$.adminConnectionSettings.id").isNotEmpty())
                .andExpect(jsonPath("$.adminConnectionSettings.serviceUrl").value(clusterToCreate.adminConnectionSettings().serviceUrl()))
                .andExpect(jsonPath("$.adminConnectionSettings.authenticationMethod", is(clusterToCreate.adminConnectionSettings().authenticationMethod().name())))
                .andExpect(jsonPath("$.adminConnectionSettings.authenticationDetails").value(clusterToCreate.adminConnectionSettings().authenticationDetails()))
                .andReturn();

        var createdCluster = jsonMapper.readValue(result.getResponse().getContentAsString(), ClusterCreation.class);
        assertThat(createdCluster.clusterInfo().clusterId()).isNotNull();

        var savedCluster = clusterRepository.findById(createdCluster.clusterInfo().clusterId());
        assertThat(savedCluster).isPresent();

        var savedClientConnectionSettings = clusterConnectionSettingsRepository.findById(savedCluster.get().getClientConnectionSettingsId());
        assertThat(savedClientConnectionSettings).isPresent();

        var savedAdminConnectionSettings = clusterConnectionSettingsRepository.findById(savedCluster.get().getAdminConnectionSettingsId());
        assertThat(savedAdminConnectionSettings).isPresent();
    }

    private ClusterCreation getValidClusterToCreate(PulsarContainer pulsarContainer, String displayName)
    {
        var clusterInfo = new ClusterInfo(null, null,
                displayName, "star", "#FFFFFF");
        var clientConnectionSettings = new ClusterConnectionSettings(null, pulsarContainer.getPulsarBrokerUrl(),
                ClusterAuthenticationMethod.NO_AUTH, null);
        var adminConnectionSettings = new ClusterConnectionSettings(null, pulsarContainer.getHttpServiceUrl(),
                ClusterAuthenticationMethod.NO_AUTH, null);

        return new ClusterCreation(clusterInfo, clientConnectionSettings, adminConnectionSettings);
    }
}