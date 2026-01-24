package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
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
 * File-based implementation of {@link ClusterConnectionSettingsRepository}.
 */
@Slf4j
@Repository
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "false", matchIfMissing = true)
public class ClusterConnectionSettingsFileRepository implements ClusterConnectionSettingsRepository
{
    private static final String CLUSTER_CONNECTION_SETTINGS_FILE = "cluster-connection-settings.json";

    private final JsonMapper jsonMapper;
    private final ObjectWriter objectWriter;
    private final File settingsFile;

    public ClusterConnectionSettingsFileRepository(JsonMapper jsonMapper,
                                                   @Value("${interfero.directories.data}") String dataDirectoryPath)
    {
        this.jsonMapper = jsonMapper;
        this.objectWriter = jsonMapper.writerWithDefaultPrettyPrinter();
        this.settingsFile = new File(dataDirectoryPath, CLUSTER_CONNECTION_SETTINGS_FILE);
    }

    @Override
    public Set<ClusterConnectionSettingsEntity> findAll()
    {
        try
        {
            log.trace("Reading all cluster connection settings from {}", settingsFile.getAbsolutePath());
            return jsonMapper.readValue(settingsFile, new TypeReference<>() {});
        }
        catch (Exception e)
        {
            log.error("Error reading cluster connection settings from {}", settingsFile.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ClusterConnectionSettingsEntity> findById(Long id)
    {
        return findById(findAll(), id);
    }

    private Optional<ClusterConnectionSettingsEntity> findById(Set<ClusterConnectionSettingsEntity> allSettings, Long id)
    {
        for (var settings : allSettings)
        {
            if (id.equals(settings.id()))
                return Optional.of(settings);
        }

        return Optional.empty();
    }

    @Override
    public ClusterConnectionSettingsEntity save(ClusterConnectionSettingsEntity clusterConnectionSettings)
    {
        Set<ClusterConnectionSettingsEntity> allSettingsToSave = new HashSet<>(findAll());
        var id = determineIdToSave(allSettingsToSave, clusterConnectionSettings);

        var clusterSettingsToSave = new ClusterConnectionSettingsEntity(id,
                clusterConnectionSettings.serviceUrl(),
                clusterConnectionSettings.authenticationMethod(),
                clusterConnectionSettings.authenticationDetails());

        allSettingsToSave.removeIf(existingEntry ->
                clusterSettingsToSave.id() != null && clusterSettingsToSave.id().equals(existingEntry.id()));
        allSettingsToSave.add(clusterSettingsToSave);

        saveAll(allSettingsToSave);
        return findById(id).orElseThrow();
    }

    private Long determineIdToSave(Set<ClusterConnectionSettingsEntity> allSettings,
                                   ClusterConnectionSettingsEntity settingsToSave)
    {
        if (settingsToSave.id() != null && findById(allSettings, settingsToSave.id()).isPresent())
            return settingsToSave.id();

        return getNextId();
    }

    private Long getNextId()
    {
        var existingSettings = findAll();
        long maxId = 0L;

        for (var settings : existingSettings)
        {
            if (settings.id() != null && settings.id() > maxId)
                maxId = settings.id();
        }

        return maxId + 1;
    }

    private void saveAll(Set<ClusterConnectionSettingsEntity> settings)
    {
        log.trace("Saving {} cluster connection settings to file {}", settings.size(), settingsFile.getAbsolutePath());

        try
        {
            FileUtils.createParentDirectories(settingsFile);
            objectWriter.writeValue(settingsFile, settings);
        }
        catch (Exception e)
        {
            log.error("Error saving cluster connection settings to {}", settingsFile.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id)
    {
        Set<ClusterConnectionSettingsEntity> allSettingsToSave = new HashSet<>(findAll());
        allSettingsToSave.removeIf(existingEntry -> id.equals(existingEntry.id()));

        saveAll(allSettingsToSave);
    }

    @Override
    public void deleteAll()
    {
        saveAll(Set.of());
    }

    @PostConstruct
    void createFileIfNotExists()
    {
        if (settingsFile.exists())
            return;

        log.trace("Creating cluster info file {}", settingsFile.getAbsolutePath());
        saveAll(Set.of());
    }
}
