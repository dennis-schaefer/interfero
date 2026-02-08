package io.interfero.clusters.controller;

import io.interfero.clusters.dtos.ClusterConnectionSettings;
import io.interfero.clusters.dtos.ClusterCreation;
import io.interfero.clusters.dtos.ClusterInfo;
import io.interfero.clusters.mappers.ClusterConnectionSettingsMapper;
import io.interfero.clusters.mappers.ClusterCreationMapper;
import io.interfero.clusters.mappers.ClusterInfoMapper;
import io.interfero.clusters.services.ClusterClientRegistry;
import io.interfero.clusters.services.ClusterCreationService;
import io.interfero.clusters.services.ClusterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/clusters")
@RequiredArgsConstructor
public class ClusterController
{
    private final ClusterService clusterService;
    private final ClusterCreationService clusterCreationService;
    private final ClusterInfoMapper clusterInfoMapper;
    private final ClusterCreationMapper clusterCreationMapper;
    private final ClusterClientRegistry clusterClientRegistry;
    private final ClusterConnectionSettingsMapper clusterConnectionSettingsMapper;

    @GetMapping
    ResponseEntity<Set<ClusterInfo>> getAllClusterInfo()
    {
        log.debug("HTTP GET '/api/clusters' called");
        var clusters = clusterService.getAll();

        var clusterInfo = clusters.stream()
                .map(clusterInfoMapper::toDto)
                .collect(Collectors.toSet());
        var response = ResponseEntity.ok(clusterInfo);

        log.debug("HTTP GET '/api/clusters' returned: {}", response);
        return response;
    }

    @GetMapping("/{clusterId}")
    ResponseEntity<ClusterInfo> getClusterInfoById(@PathVariable String clusterId)
    {
        log.debug("HTTP GET '/api/clusters/{}' called", clusterId);
        var cluster = clusterService.getById(clusterId);
        ResponseEntity<ClusterInfo> response = ResponseEntity.notFound().build();

        if (cluster.isPresent())
        {
            var clusterInfo = clusterInfoMapper.toDto(cluster.get());
            response = ResponseEntity.ok(clusterInfo);
        }

        log.debug("HTTP GET '/api/clusters/{}' returned: {}", clusterId, response);
        return response;
    }

    @PostMapping
    ResponseEntity<ClusterCreation> createCluster(@Valid @RequestBody ClusterCreation dtoToCreate)
    {
        log.debug("HTTP POST '/api/clusters' called with body: {}", dtoToCreate);
        var entityToCreate = clusterCreationMapper.toEntity(dtoToCreate);
        var clusterCreation = clusterCreationService.createCluster(entityToCreate);
        var createdDto = clusterCreationMapper.toDto(clusterCreation);

        var uri = URI.create("/api/clusters/" + createdDto.clusterInfo().clusterId());
        var response = ResponseEntity.created(uri).body(createdDto);

        log.debug("HTTP POST '/api/clusters' returned: {}", response);
        return response;
    }

    @PostMapping("/connections/admin/verify")
    ResponseEntity<Void> verifyAdminConnection(@RequestBody ClusterConnectionSettings clusterConnectionSettings)
    {
        log.debug("HTTP POST '/api/clusters/connections/admin/verify' called");
        var entity = clusterConnectionSettingsMapper.toEntity(clusterConnectionSettings);

        clusterClientRegistry.verifyAdminConnection(entity);
        ResponseEntity<Void> response = ResponseEntity.ok().build();

        log.debug("HTTP POST '/api/clusters/connections/admin/verify' returned: {}", response);
        return response;
    }

    @PostMapping("/connections/client/verify")
    ResponseEntity<Void> verifyClientConnection(@RequestBody ClusterConnectionSettings clusterConnectionSettings)
    {
        log.debug("HTTP POST '/api/clusters/connections/client/verify' called");
        var entity = clusterConnectionSettingsMapper.toEntity(clusterConnectionSettings);

        clusterClientRegistry.verifyClientConnection(entity);
        ResponseEntity<Void> response = ResponseEntity.ok().build();

        log.debug("HTTP POST '/api/clusters/connections/client/verify' returned: {}", response);
        return response;
    }
}
