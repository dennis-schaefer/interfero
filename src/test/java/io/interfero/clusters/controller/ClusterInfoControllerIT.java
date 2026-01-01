package io.interfero.clusters.controller;

import io.interfero.TestcontainersConfiguration;
import io.interfero.clusters.domain.ClusterInfo;
import io.interfero.clusters.repositories.ClusterInfoRepository;
import org.hamcrest.Matchers;
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
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles({"it"})
class ClusterInfoControllerIT
{
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ClusterInfoRepository clusterInfoRepository;

    private final JsonMapper jsonMapper = new JsonMapper();

    private MockMvc mockMvc;

    @BeforeEach
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
        clusterInfoRepository.deleteAll();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldSaveAndRetrieveClusterInfo() throws Exception
    {
        var clusterInfo = new ClusterInfo("cluster-a", "standalone", "Cluster A", "icon-a", "#FF00FF");
        var clusterInfoJson = jsonMapper.writeValueAsString(clusterInfo);

        mockMvc.perform(put("/api/cluster-info/cluster-a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clusterInfoJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("cluster-a"))
                .andExpect(jsonPath("$.internalName").value("standalone"))
                .andExpect(jsonPath("$.displayName").value("Cluster A"))
                .andExpect(jsonPath("$.icon").value("icon-a"))
                .andExpect(jsonPath("$.color").value("#FF00FF"));

        mockMvc.perform(get("/api/cluster-info/cluster-a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("cluster-a"))
                .andExpect(jsonPath("$.internalName").value("standalone"))
                .andExpect(jsonPath("$.displayName").value("Cluster A"))
                .andExpect(jsonPath("$.icon").value("icon-a"))
                .andExpect(jsonPath("$.color").value("#FF00FF"));

        mockMvc.perform(get("/api/cluster-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Matchers.hasSize(2)))
                .andDo(result -> assertThat((result.getResponse().getContentAsString())).contains(clusterInfoJson));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotAllowNonAdminToSaveClusterInfo() throws Exception
    {
        var clusterInfo = new ClusterInfo("cluster-b", "standalone", "Cluster B", "icon-b", "#00FF00");
        var clusterInfoJson = jsonMapper.writeValueAsString(clusterInfo);

        mockMvc.perform(put("/api/cluster-info/cluster-b")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clusterInfoJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}