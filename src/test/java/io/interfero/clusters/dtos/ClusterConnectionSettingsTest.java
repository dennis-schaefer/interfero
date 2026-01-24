package io.interfero.clusters.dtos;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static io.interfero.clusters.domain.ClusterAuthenticationMethod.NO_AUTH;
import static org.assertj.core.api.Assertions.assertThat;

class ClusterConnectionSettingsTest
{
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldBeValid()
    {
        var settings1 = new ClusterConnectionSettings(null, "pulsar://localhost:6650", NO_AUTH, null);
        var settings2 = new ClusterConnectionSettings(42L, "pulsar://localhost:6650", NO_AUTH, "");
        var settings3 = new ClusterConnectionSettings(null, "pulsar://localhost:6650", NO_AUTH, "  ");
        var settings4 = new ClusterConnectionSettings(99L, "pulsar://localhost:6650", NO_AUTH, "{}");

        assertThat(validator.validate(settings1)).isEmpty();
        assertThat(validator.validate(settings2)).isEmpty();
        assertThat(validator.validate(settings3)).isEmpty();
        assertThat(validator.validate(settings4)).isEmpty();
    }

    @Test
    void shouldHaveInvalidServiceUrl()
    {
        var settings1 = new ClusterConnectionSettings(null, null, NO_AUTH, null);
        var settings2 = new ClusterConnectionSettings(null, "", NO_AUTH, null);
        var settings3 = new ClusterConnectionSettings(null, "   ", NO_AUTH, null);

        var settingsViolations1 = validator.validate(settings1);
        var settingsViolations2 = validator.validate(settings2);
        var settingsViolations3 = validator.validate(settings3);

        assertThat(settingsViolations1).hasSize(2);
        assertThat(settingsViolations1.stream().toList().get(0).getPropertyPath().toString()).isEqualTo("serviceUrl");
        assertThat(settingsViolations1.stream().toList().get(1).getPropertyPath().toString()).isEqualTo("serviceUrl");
        assertThat(settingsViolations2).hasSize(1);
        assertThat(settingsViolations2.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("serviceUrl");
        assertThat(settingsViolations3).hasSize(1);
        assertThat(settingsViolations3.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("serviceUrl");
    }

    @Test
    void shouldHaveInvalidAuthenticationMethod()
    {
        var settings = new ClusterConnectionSettings(null, "pulsar://localhost:6650", null, null);

        var settingsViolations = validator.validate(settings);

        assertThat(settingsViolations).hasSize(1);
        assertThat(settingsViolations.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("authenticationMethod");
    }
}