package io.interfero.clusters.services;

import io.interfero.clusters.domain.ClusterAuthenticationMethod;
import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import io.interfero.clusters.domain.ClusterEntity;
import io.interfero.clusters.events.ClusterClientsRegisteredEvent;
import io.interfero.clusters.events.ClusterClientsUnregisteredEvent;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterClientRegistryTest
{
    @Mock
    private ClusterConnectionSettingsService connectionSettingsService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ClusterClientRegistry clusterClientRegistry;

    @Test
    void shouldReturnPulsarAdminForCluster() throws PulsarClientException
    {
        var clusterId = "1234ABCD";
        var mockPulsarAdmins = new ConcurrentHashMap<>();
        var mockPulsarAdmin = PulsarAdmin.builder().serviceHttpUrl("http://localhost:8080").build();
        mockPulsarAdmins.put(clusterId, mockPulsarAdmin);
        ReflectionTestUtils.setField(clusterClientRegistry, "pulsarAdmins", mockPulsarAdmins);

        var retrievedPulsarAdmin = clusterClientRegistry.getPulsarAdminForCluster(clusterId);

        assertThat(retrievedPulsarAdmin).isPresent();
        assertThat(retrievedPulsarAdmin.get()).isEqualTo(mockPulsarAdmin);
    }

    @Test
    void shouldReturnEmptyWhenNoPulsarAdminForCluster()
    {
        var clusterId = "NON_EXISTENT_CLUSTER";

        var retrievedPulsarAdmin = clusterClientRegistry.getPulsarAdminForCluster(clusterId);

        assertThat(retrievedPulsarAdmin).isNotPresent();
    }

    @Test
    void shouldRegisterClientsForCluster() throws PulsarClientException
    {
        var clusterId = "1234ABCD";
        var mockPulsarClients = new ConcurrentHashMap<>();
        var mockPulsarAdmins = new ConcurrentHashMap<>();
        var mockPulsarClient = PulsarClient.builder().serviceUrl("pulsar://oldhost:6650").build();
        var mockPulsarAdmin = PulsarAdmin.builder().serviceHttpUrl("http://oldhost:8080").build();
        mockPulsarClients.put(clusterId, mockPulsarClient);
        mockPulsarAdmins.put(clusterId, mockPulsarAdmin);
        ReflectionTestUtils.setField(clusterClientRegistry, "pulsarClients", mockPulsarClients);
        ReflectionTestUtils.setField(clusterClientRegistry, "pulsarAdmins", mockPulsarAdmins);

        var clientConnectionSettings = new ClusterConnectionSettingsEntity(1L, "pulsar://localhost:6650",
                ClusterAuthenticationMethod.NO_AUTH, "{}");
        var adminConnectionSettings = new ClusterConnectionSettingsEntity(2L, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{}");
        var clusterEntity = new ClusterEntity(clusterId, "Test Cluster", "star", "#123456",
                1L, 2L);
        when(connectionSettingsService.findById(1L)).thenReturn(Optional.of(clientConnectionSettings));
        when(connectionSettingsService.findById(2L)).thenReturn(Optional.of(adminConnectionSettings));

        // Before registration
        var registeredPulsarAdmin = clusterClientRegistry.getPulsarAdminForCluster(clusterId);
        assertThat(registeredPulsarAdmin).isPresent();
        assertThat(registeredPulsarAdmin.get().getServiceUrl()).isEqualTo("http://oldhost:8080");

        // Register clients
        clusterClientRegistry.registerClientsForCluster(clusterEntity);

        verify(connectionSettingsService).findById(1L);
        verify(connectionSettingsService).findById(2L);
        verify(eventPublisher).publishEvent(any(ClusterClientsUnregisteredEvent.class));
        verify(eventPublisher).publishEvent(any(ClusterClientsRegisteredEvent.class));

        // After registration
        registeredPulsarAdmin = clusterClientRegistry.getPulsarAdminForCluster(clusterId);
        assertThat(registeredPulsarAdmin).isPresent();
        assertThat(registeredPulsarAdmin.get().getServiceUrl()).isEqualTo(adminConnectionSettings.serviceUrl());
    }

    @Test
    void shouldUnregisterAndRegisterClientsForCluster()
    {
        var clientConnectionSettings = new ClusterConnectionSettingsEntity(1L, "pulsar://localhost:6650",
                ClusterAuthenticationMethod.NO_AUTH, "{}");
        var adminConnectionSettings = new ClusterConnectionSettingsEntity(2L, "http://localhost:8080",
                ClusterAuthenticationMethod.NO_AUTH, "{}");
        var clusterEntity = new ClusterEntity("1234ABCD", "Test Cluster", "star", "#123456",
                1L, 2L);
        when(connectionSettingsService.findById(1L)).thenReturn(Optional.of(clientConnectionSettings));
        when(connectionSettingsService.findById(2L)).thenReturn(Optional.of(adminConnectionSettings));

        clusterClientRegistry.registerClientsForCluster(clusterEntity);

        verify(connectionSettingsService).findById(1L);
        verify(connectionSettingsService).findById(2L);
        verify(eventPublisher).publishEvent(any(ClusterClientsUnregisteredEvent.class));
        verify(eventPublisher).publishEvent(any(ClusterClientsRegisteredEvent.class));
    }

    @Test
    void shouldFailToRegisterClientsForClusterWhenConnectionSettingsInvalid()
    {
        var clusterEntity = new ClusterEntity("1234ABCD", "Test Cluster", "star", "#123456",
                1L, 2L);
        when(connectionSettingsService.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clusterClientRegistry.registerClientsForCluster(clusterEntity))
                .isInstanceOf(IllegalArgumentException.class);
    }
}