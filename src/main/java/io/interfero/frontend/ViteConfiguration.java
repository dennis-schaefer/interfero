package io.interfero.frontend;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("interfero.frontend.vite")
public class ViteConfiguration
{
    private final boolean autostartEnabled;
    private final String devServerUrl;
    private final String directory;
    private final String startupCommand;

    public ViteConfiguration(Boolean autostartEnabled, String devServerUrl, String directory, String startupCommand)
    {
        this.autostartEnabled = autostartEnabled == null || autostartEnabled;
        this.devServerUrl = devServerUrl == null ? "http://localhost:3000/" : devServerUrl;
        this.directory = directory == null ? "src/main/frontend" : directory;
        this.startupCommand = startupCommand == null ? "npm run dev" : startupCommand;
    }
}
