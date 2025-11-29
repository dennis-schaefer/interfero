package io.interfero.frontend;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("interfero.frontend.static-resources")
public class StaticResourcesConfiguration
{
    private final String directory;
    private final boolean cleanOnStartup;

    public StaticResourcesConfiguration(String directory, Boolean cleanOnStartup)
    {
        this.directory = directory == null ? "src/main/resources/static" : directory;
        this.cleanOnStartup = cleanOnStartup != null && cleanOnStartup;
    }
}
