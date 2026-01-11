package io.interfero.clusters.dtos;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static io.interfero.clusters.domain.ClusterAuthenticationMethod.NO_AUTH;
import static org.assertj.core.api.Assertions.assertThat;

class ClusterCreationTest
{
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldBeValid()
    {
        var clusterInfo = new ClusterInfo(null, null, "Test Cluster", "star", "#FF12AB");
        var clientSettings = new ClusterConnectionSettings(null, "pulsar://localhost:6650", NO_AUTH, null);
        var adminSettings = new ClusterConnectionSettings(null, "http://localhost:8080", NO_AUTH, null);
        var clusterCreation = new ClusterCreation(clusterInfo, clientSettings, adminSettings);

        assertThat(validator.validate(clusterCreation).isEmpty());
    }

    @Test
    void shouldBeInvalidWhenClusterInfoIsInvalid()
    {
        var clusterInfo = new ClusterInfo(null, null, "Test Cluster", "star", "invalid-color");
        var clientSettings = new ClusterConnectionSettings(null, "pulsar://localhost:6650", NO_AUTH, null);
        var adminSettings = new ClusterConnectionSettings(null, "http://localhost:8080", NO_AUTH, null);
        var clusterCreation = new ClusterCreation(clusterInfo, clientSettings, adminSettings);

        var violations = validator.validate(clusterCreation);
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().findFirst().get().getPropertyPath().toString()).isEqualTo("clusterInfo.color");
    }

    @Test
    void shouldBeInvalidWhenClientSettingsAreInvalid()
    {
        var clusterInfo = new ClusterInfo(null, null, "Test Cluster", "star", "#FF12AB");
        var clientSettings = new ClusterConnectionSettings(null, "", NO_AUTH, null);
        var adminSettings = new ClusterConnectionSettings(null, "http://localhost:8080", NO_AUTH, null);
        var clusterCreation = new ClusterCreation(clusterInfo, clientSettings, adminSettings);

        var violations = validator.validate(clusterCreation);
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().findFirst().get().getPropertyPath().toString()).isEqualTo("clientConnectionSettings.serviceUrl");
    }

    @Test
    void shouldBeInvalidWhenAdminSettingsAreInvalid()
    {
        var clusterInfo = new ClusterInfo(null, null, "Test Cluster", "star", "#FF12AB");
        var clientSettings = new ClusterConnectionSettings(null, "pulsar://localhost:6650", NO_AUTH, null);
        var adminSettings = new ClusterConnectionSettings(null, "", NO_AUTH, null);
        var clusterCreation = new ClusterCreation(clusterInfo, clientSettings, adminSettings);

        var violations = validator.validate(clusterCreation);
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().findFirst().get().getPropertyPath().toString()).isEqualTo("adminConnectionSettings.serviceUrl");
    }
}