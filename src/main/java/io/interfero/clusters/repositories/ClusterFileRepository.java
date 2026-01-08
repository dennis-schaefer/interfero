package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * File-based implementation of {@link ClusterRepository}.
 */
@Slf4j
@Repository
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "false", matchIfMissing = true)
public class ClusterFileRepository implements ClusterRepository
{
    private static final String CLUSTER_FILE = "cluster.json";

    private final JsonMapper jsonMapper;
    private final ObjectWriter objectWriter;
    private final File clusterFile;

    public ClusterFileRepository(JsonMapper jsonMapper,
                                 @Value("${interfero.directories.data}") String dataDirectoryPath)
    {
        this.jsonMapper = jsonMapper;
        this.objectWriter = jsonMapper.writerWithDefaultPrettyPrinter();
        this.clusterFile = new File(dataDirectoryPath, CLUSTER_FILE);
    }

    @Override
    public Set<ClusterEntity> findAll()
    {
        try
        {
            log.trace("Reading all clusters from {}", clusterFile.getAbsolutePath());
            return jsonMapper.readValue(clusterFile, new TypeReference<>() {});
        }
        catch (Exception e)
        {
            log.error("Error reading clusters from {}", clusterFile.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ClusterEntity> findById(String id)
    {
        var clusters = findAll();

        return clusters.stream()
                .filter(cluster -> cluster.getId().equals(id))
                .findFirst();
    }

    @Override
    public ClusterEntity save(ClusterEntity cluster)
    {
        Set<ClusterEntity> clustersToSave = new HashSet<>(findAll());

        clustersToSave.removeIf(entry -> entry.getId().equals(cluster.getId()));
        clustersToSave.add(cluster);
        saveAll(clustersToSave);

        return findById(cluster.getId()).orElseThrow();
    }

    private void saveAll(Set<ClusterEntity> clusters)
    {
        log.trace("Saving {} clusters to file {}", clusters.size(), clusterFile.getAbsolutePath());

        try
        {
            FileUtils.createParentDirectories(clusterFile);
            objectWriter.writeValue(clusterFile, clusters);
        }
        catch (Exception e)
        {
            log.error("Error saving clusters to {}", clusterFile.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String id)
    {
        Set<ClusterEntity> clustersToSave = new HashSet<>(findAll());
        clustersToSave.removeIf(entry -> entry.getId().equals(id));

        saveAll(clustersToSave);
    }

    @Override
    public void deleteAll()
    {
        saveAll(Set.of());
    }

    @PostConstruct
    void createFileIfNotExists()
    {
        if (clusterFile.exists())
            return;

        log.trace("Creating cluster info file {}", clusterFile.getAbsolutePath());
        saveAll(Set.of());
    }
}
