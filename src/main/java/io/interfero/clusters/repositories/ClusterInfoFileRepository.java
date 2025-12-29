package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfoRecord;
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
    public Set<ClusterInfoRecord> findAll()
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
    public Optional<ClusterInfoRecord> findByName(String name)
    {
        var existingClusterInfoRecords = findAll();
        for (ClusterInfoRecord clusterInfoRecord : existingClusterInfoRecords)
        {
            if (clusterInfoRecord.name().equals(name))
                return Optional.of(clusterInfoRecord);
        }

        return Optional.empty();
    }

    @Override
    public ClusterInfoRecord save(ClusterInfoRecord clusterInfoRecord)
    {
        var existingClusterInfoRecords = findAll();
        Set<ClusterInfoRecord> clusterInfoRecordRecordsToSave = new HashSet<>();

        for (ClusterInfoRecord existingClusterInfoRecord : existingClusterInfoRecords)
        {
            if (!existingClusterInfoRecord.name().equals(clusterInfoRecord.name()))
                clusterInfoRecordRecordsToSave.add(existingClusterInfoRecord);
        }

        clusterInfoRecordRecordsToSave.add(clusterInfoRecord);
        saveAll(clusterInfoRecordRecordsToSave);
        return findByName(clusterInfoRecord.name()).orElseThrow();
    }

    private void saveAll(Set<ClusterInfoRecord> clusterInfoRecordRecords)
    {
        var clusterNames = clusterInfoRecordRecords.stream().map(ClusterInfoRecord::name).toList();
        log.trace("Saving {} cluster info records to file {}: {}", clusterInfoRecordRecords.size(),
                clusterInfoFile.getAbsolutePath(), clusterNames);
        objectWriter.writeValue(clusterInfoFile, clusterInfoRecordRecords);
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
