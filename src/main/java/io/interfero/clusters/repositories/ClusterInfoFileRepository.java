package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
 * File-based implementation of the ClusterInfoRepository interface.
 */
@Slf4j
@Repository
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "false", matchIfMissing = true)
public class ClusterInfoFileRepository implements ClusterInfoRepository
{
    private static final String CLUSTER_INFO_FILE = "cluster-info.json";

    private final JsonMapper jsonMapper;
    private final ObjectWriter objectWriter;
    private final File clusterInfoFile;

    public ClusterInfoFileRepository(JsonMapper jsonMapper,
                                     @Value("${interfero.directories.data}") String dataDirectoryPath)
    {
        this.jsonMapper = jsonMapper;
        this.objectWriter = jsonMapper.writerWithDefaultPrettyPrinter();
        this.clusterInfoFile = new File(dataDirectoryPath, CLUSTER_INFO_FILE);
    }

    @Override
    public Set<ClusterInfo> findAll()
    {
        try
        {
            log.trace("Reading all cluster info records from file {}", clusterInfoFile.getAbsolutePath());
            return jsonMapper.readValue(clusterInfoFile, new TypeReference<>() {});
        }
        catch (Exception e)
        {
            log.error("Error reading cluster info records from file {}", clusterInfoFile.getAbsolutePath(), e);
            throw e;
        }
    }

    @Override
    public Optional<ClusterInfo> findByName(String name)
    {
        var existingClusterInfoRecords = findAll();
        for (ClusterInfo clusterInfo : existingClusterInfoRecords)
        {
            if (clusterInfo.name().equals(name))
                return Optional.of(clusterInfo);
        }

        return Optional.empty();
    }

    @Override
    public ClusterInfo save(ClusterInfo clusterInfo)
    {
        var existingClusterInfoRecords = findAll();
        Set<ClusterInfo> clusterInfoRecordsToSave = new HashSet<>();

        for (ClusterInfo existingClusterInfo : existingClusterInfoRecords)
        {
            if (!existingClusterInfo.name().equals(clusterInfo.name()))
                clusterInfoRecordsToSave.add(existingClusterInfo);
        }

        clusterInfoRecordsToSave.add(clusterInfo);
        saveAll(clusterInfoRecordsToSave);
        return findByName(clusterInfo.name()).orElseThrow();
    }

    private void saveAll(Set<ClusterInfo> clusterInfoRecords)
    {
        var clusterNames = clusterInfoRecords.stream().map(ClusterInfo::name).toList();
        log.trace("Saving {} cluster info records to file {}: {}", clusterInfoRecords.size(),
                clusterInfoFile.getAbsolutePath(), clusterNames);
        objectWriter.writeValue(clusterInfoFile, clusterInfoRecords);
    }

    @PostConstruct
    void createFileIfNotExists()
    {
        if (clusterInfoFile.exists())
            return;

        log.debug("Creating cluster info file {}", clusterInfoFile.getAbsolutePath());
        saveAll(Set.of());
    }
}
