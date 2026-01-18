package io.interfero.clusters.controller;

import io.interfero.clusters.dtos.ClusterCreation;
import io.interfero.clusters.dtos.ClusterInfo;
import io.interfero.clusters.mappers.ClusterCreationMapper;
import io.interfero.clusters.mappers.ClusterInfoMapper;
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

    @GetMapping
    ResponseEntity<Set<ClusterInfo>> getAllClusterInfo()
    {
        log.debug("HTTP GET '/api/clusters' called");
        var clusters = clusterService.getAll();

        var clusterInfo = clusters.stream()
                .map(clusterInfoMapper::toDto)
                .collect(Collectors.toSet());
        var response = ResponseEntity.ok(clusterInfo);

        log.debug("HTTP GET '/api/clusters' called returned: {}", response);
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

        log.debug("HTTP GET '/api/clusters/{}' called returned: {}", clusterId, response);
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

        log.debug("HTTP POST '/api/clusters' called returned: {}", response);
        return response;
    }
}
