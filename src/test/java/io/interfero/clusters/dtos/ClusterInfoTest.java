package io.interfero.clusters.dtos;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterInfoTest
{
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldBeValid()
    {
        var clusterInfo1 = new ClusterInfo(null, null, "Cluster", "star", "#FF5733");
        var clusterInfo2 = new ClusterInfo("123456", null, "Cluster", "star", "#FF5733");
        var clusterInfo3 = new ClusterInfo(null, "standalone", "Cluster", "star", "#FF5733");
        var clusterInfo4 = new ClusterInfo("ABCD1234", "cluster", "Cluster", "star", "#FF5733");

        assertThat(validator.validate(clusterInfo1)).isEmpty();
        assertThat(validator.validate(clusterInfo2)).isEmpty();
        assertThat(validator.validate(clusterInfo3)).isEmpty();
        assertThat(validator.validate(clusterInfo4)).isEmpty();
    }

    @Test
    void shouldHaveInvalidDisplayName()
    {
        var clusterInfo1 = new ClusterInfo(null, null, null, "star", "#FF5733");
        var clusterInfo2 = new ClusterInfo(null, null, "", "star", "#FF5733");
        var clusterInfo3 = new ClusterInfo(null, null, "   ", "star", "#FF5733");

        var violations1 = validator.validate(clusterInfo1);
        var violations2 = validator.validate(clusterInfo2);
        var violations3 = validator.validate(clusterInfo3);

        assertThat(violations1).hasSize(2);
        assertThat(violations1.stream().toList().get(0).getPropertyPath().toString()).isEqualTo("displayName");
        assertThat(violations1.stream().toList().get(1).getPropertyPath().toString()).isEqualTo("displayName");
        assertThat(violations2).hasSize(1);
        assertThat(violations2.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("displayName");
        assertThat(violations3).hasSize(1);
        assertThat(violations3.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("displayName");
    }

    @Test
    void shouldHaveInvalidIcon()
    {
        var clusterInfo1 = new ClusterInfo(null, null, "Cluster", null, "#FF5733");
        var clusterInfo2 = new ClusterInfo(null, null, "Cluster", "", "#FF5733");
        var clusterInfo3 = new ClusterInfo(null, null, "Cluster", "   ", "#FF5733");

        var violations1 = validator.validate(clusterInfo1);
        var violations2 = validator.validate(clusterInfo2);
        var violations3 = validator.validate(clusterInfo3);

        assertThat(violations1).hasSize(2);
        assertThat(violations1.stream().toList().get(0).getPropertyPath().toString()).isEqualTo("icon");
        assertThat(violations1.stream().toList().get(1).getPropertyPath().toString()).isEqualTo("icon");
        assertThat(violations2).hasSize(1);
        assertThat(violations2.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("icon");
        assertThat(violations3).hasSize(1);
        assertThat(violations3.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("icon");
    }

    @Test
    void shouldHaveInvalidColor()
    {
        var clusterInfo1 = new ClusterInfo(null, null, "Cluster", "star", null);
        var clusterInfo2 = new ClusterInfo(null, null, "Cluster", "star", "");
        var clusterInfo3 = new ClusterInfo(null, null, "Cluster", "star", "   ");
        var clusterInfo4 = new ClusterInfo(null, null, "Cluster", "star", "FF5733");
        var clusterInfo5 = new ClusterInfo(null, null, "Cluster", "star", "#FFF");

        var violations1 = validator.validate(clusterInfo1);
        var violations2 = validator.validate(clusterInfo2);
        var violations3 = validator.validate(clusterInfo3);
        var violations4 = validator.validate(clusterInfo4);
        var violations5 = validator.validate(clusterInfo5);

        assertThat(violations1).hasSize(2);
        assertThat(violations1.stream().toList().get(0).getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations1.stream().toList().get(1).getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations2).hasSize(2);
        assertThat(violations2.stream().toList().get(0).getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations2.stream().toList().get(1).getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations3).hasSize(2);
        assertThat(violations3.stream().toList().get(0).getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations3.stream().toList().get(1).getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations4).hasSize(1);
        assertThat(violations4.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations4).hasSize(1);
        assertThat(violations4.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("color");
        assertThat(violations5).hasSize(1);
        assertThat(violations5.stream().toList().getFirst().getPropertyPath().toString()).isEqualTo("color");
    }
}