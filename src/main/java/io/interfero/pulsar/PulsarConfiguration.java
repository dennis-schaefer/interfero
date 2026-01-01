package io.interfero.pulsar;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.pulsar.autoconfigure.PulsarProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Slf4j
@Getter
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "interfero.pulsar")
public class PulsarConfiguration
{
    private final Map<String, PulsarProperties> clusters = new HashMap<>();

    @PostConstruct
    public void printClusters()
    {
        if (clusters.isEmpty())
        {
            log.warn("No Pulsar clusters configured! Please check the documentation for configuration instructions.");
            return;
        }

        log.debug("{} Pulsar clusters configured: {}", clusters.size(), clusters.keySet());
    }
}
