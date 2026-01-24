package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.repositories.ClusterConnectionSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterConnectionSettingsServiceTest
{
    @Mock
    private ClusterConnectionSettingsRepository connectionSettingsRepository;

    @InjectMocks
    private ClusterConnectionSettingsService connectionSettingsService;

    @Test
    void shouldCreateClusterConnectionSettings()
    {
        when(connectionSettingsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var settingsToCreate = new ClusterConnectionSettingsEntity(null, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{}");

        var createdSettings = connectionSettingsService.create(settingsToCreate);

        assertThat(createdSettings).isNotNull();
        assertThat(createdSettings.serviceUrl()).isEqualTo(settingsToCreate.serviceUrl());
        assertThat(createdSettings.authenticationMethod()).isEqualTo(settingsToCreate.authenticationMethod());
        assertThat(createdSettings.authenticationDetails()).isEqualTo(settingsToCreate.authenticationDetails());
    }

    @Test
    void shouldFailToCreateClusterConnectionSettingsWhenRepositoryFails()
    {
        when(connectionSettingsRepository.save(any())).thenThrow(new RuntimeException("Database error"));
        var settingsToCreate = new ClusterConnectionSettingsEntity(null, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{}");

        assertThatThrownBy(() -> connectionSettingsService.create(settingsToCreate))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }

    @Test
    void shouldFindClusterConnectionSettingsById()
    {
        var existingSettings = new ClusterConnectionSettingsEntity(1L, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{}");
        when(connectionSettingsRepository.findById(1L)).thenReturn(Optional.of(existingSettings));

        var foundSettings = connectionSettingsService.findById(1L);

        assertThat(foundSettings).isPresent();
        assertThat(foundSettings.get()).isEqualTo(existingSettings);
    }

    @Test
    void shouldNotFindClusterConnectionSettingsById()
    {
        when(connectionSettingsRepository.findById(1L)).thenReturn(Optional.empty());

        var foundSettings = connectionSettingsService.findById(1L);

        assertThat(foundSettings).isNotPresent();
    }
}