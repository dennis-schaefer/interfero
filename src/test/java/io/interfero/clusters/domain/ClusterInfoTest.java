package io.interfero.clusters.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterInfoTest
{
    @Test
    void shouldBeFullyDefined()
    {
        var clusterInfo = new ClusterInfo("name", "internalName", "displayName", "icon", "color");
        assertThat(clusterInfo.isFullyDefined()).isTrue();
    }

    @Test
    void shouldNotBeFullyDefinedWithMissingDisplayName()
    {
        var clusterInfoWithNullDisplayName = new ClusterInfo("name", "internalName", null, "icon", "color");
        assertThat(clusterInfoWithNullDisplayName.isFullyDefined()).isFalse();

        var clusterInfoWithBlankDisplayName = new ClusterInfo("name", "internalName", "   ", "icon", "color");
        assertThat(clusterInfoWithBlankDisplayName.isFullyDefined()).isFalse();
    }

    @Test
    void shouldNotBeFullyDefinedWithMissingIcon()
    {
        var clusterInfoWithNullIcon = new ClusterInfo("name", "internalName", "displayName", null, "color");
        assertThat(clusterInfoWithNullIcon.isFullyDefined()).isFalse();

        var clusterInfoWithBlankIcon = new ClusterInfo("name", "internalName", "displayName", "   ", "color");
        assertThat(clusterInfoWithBlankIcon.isFullyDefined()).isFalse();
    }

    @Test
    void shouldNotBeFullyDefinedWithMissingColor()
    {
        var clusterInfoWithNullColor = new ClusterInfo("name", "internalName", "displayName", "icon", null);
        assertThat(clusterInfoWithNullColor.isFullyDefined()).isFalse();

        var clusterInfoWithBlankColor = new ClusterInfo("name", "internalName", "displayName", "icon", "   ");
        assertThat(clusterInfoWithBlankColor.isFullyDefined()).isFalse();
    }
}