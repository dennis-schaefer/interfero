package io.interfero.clusters.services;

import io.interfero.clusters.repositories.ClusterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterInitializer {
    private final ClusterRepository clusterRepository;
    private final ClusterClientRegistry clientRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeClients() {
        log.info("Initializing Pulsar clients and admins for configured clusters...");
        var clusters = clusterRepository.findAll();

        for (var cluster : clusters) {
            try {
                clientRegistry.registerClientsForCluster(cluster);
            } catch (Exception e) {
                log.error("Failed to initialize Pulsar clients for cluster: {}", cluster.getId(), e);
            }
        }
    }
}
