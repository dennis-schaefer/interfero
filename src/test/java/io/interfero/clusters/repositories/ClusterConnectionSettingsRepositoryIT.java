package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ClusterConnectionSettingsRepositoryIT
{
    protected ClusterConnectionSettingsRepository clusterConnectionSettingsRepository;

    @AfterEach
    void tearDown()
    {
        clusterConnectionSettingsRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindClusterConnectionSettings()
    {
        var settingsToSave = new ClusterConnectionSettingsEntity(null, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{\"token\": \"abc123\"}");

        var savedSettings = clusterConnectionSettingsRepository.save(settingsToSave);
        assertThat(savedSettings.id()).isNotNull();
        shouldBeEqual(settingsToSave, savedSettings);

        var allSettings = clusterConnectionSettingsRepository.findAll();
        assertThat(allSettings).isNotNull();
        assertThat(allSettings).contains(savedSettings);

        var settingsById = clusterConnectionSettingsRepository.findById(savedSettings.id());
        assertThat(settingsById).isPresent();
        shouldBeEqual(savedSettings, settingsById.get());

        var settingsToUpdate = new ClusterConnectionSettingsEntity(savedSettings.id(),
                "http://localhost:9090", ClusterAuthenticationMethod.NO_AUTH, "{\"username\": \"user\", \"password\": \"pass\"}");

        var updatedSettings = clusterConnectionSettingsRepository.save(settingsToUpdate);
        assertThat(updatedSettings.id()).isNotNull();
        shouldBeEqual(settingsToUpdate, updatedSettings);

        settingsById = clusterConnectionSettingsRepository.findById(updatedSettings.id());
        assertThat(settingsById).isPresent();
        shouldBeEqual(updatedSettings, settingsById.get());
    }

    @Test
    void shouldNotFindNonExistingClusterConnectionSettings()
    {
        var settingsById = clusterConnectionSettingsRepository.findById(9999L);
        assertThat(settingsById).isNotPresent();
    }

    @Test
    void shouldDeleteById()
    {
        var settingsToSave = new ClusterConnectionSettingsEntity(null, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{\"token\": \"abc123\"}");

        var savedSettings = clusterConnectionSettingsRepository.save(settingsToSave);
        assertThat(savedSettings.id()).isNotNull();

        clusterConnectionSettingsRepository.deleteById(savedSettings.id());

        var settingsById = clusterConnectionSettingsRepository.findById(savedSettings.id());
        assertThat(settingsById).isNotPresent();
    }

    @Test
    void shouldDeleteAllClusterConnectionSettings()
    {
        var settings1 = new ClusterConnectionSettingsEntity(null, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{\"token\": \"abc123\"}");
        var settings2 = new ClusterConnectionSettingsEntity(null, "http://localhost:9090",
                ClusterAuthenticationMethod.NO_AUTH, "{\"username\": \"user\", \"password\": \"pass\"}");

        clusterConnectionSettingsRepository.save(settings1);
        clusterConnectionSettingsRepository.save(settings2);

        var allSettings = clusterConnectionSettingsRepository.findAll();
        assertThat(allSettings).hasSize(2);

        clusterConnectionSettingsRepository.deleteAll();

        allSettings = clusterConnectionSettingsRepository.findAll();
        assertThat(allSettings).isEmpty();
    }

    void shouldBeEqual(ClusterConnectionSettingsEntity expected, ClusterConnectionSettingsEntity actual)
    {
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isNotNull();
        assertThat(actual.serviceUrl()).isEqualTo(expected.serviceUrl());
        assertThat(actual.authenticationMethod()).isEqualTo(expected.authenticationMethod());
        assertThat(actual.authenticationDetails()).isEqualTo(expected.authenticationDetails());
    }
}