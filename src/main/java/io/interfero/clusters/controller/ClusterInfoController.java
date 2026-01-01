package io.interfero.clusters.controller;

import io.interfero.clusters.domain.ClusterInfo;
import io.interfero.clusters.services.ClusterInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/cluster-info")
@RequiredArgsConstructor
public class ClusterInfoController
{
    private final ClusterInfoService clusterInfoService;

    @GetMapping()
    public Set<ClusterInfo> getAllClusterInfo()
    {
        return clusterInfoService.getClusterInfos();
    }

    @GetMapping("/{clusterName}")
    public ResponseEntity<ClusterInfo> getClusterInfoByName(@PathVariable String clusterName)
    {
        return clusterInfoService.getClusterInfoByName(clusterName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{clusterName}")
    public ResponseEntity<ClusterInfo> updateClusterInfo(@PathVariable String clusterName,
                                                         @RequestBody ClusterInfo clusterInfo)
    {
        if (!clusterName.equals(clusterInfo.name()))
            return ResponseEntity.badRequest().build();

        if (clusterInfoService.getClusterInfoByName(clusterName).isEmpty())
            return ResponseEntity.notFound().build();

        var updatedInfo = clusterInfoService.saveClusterInfo(clusterInfo);
        return ResponseEntity.ok(updatedInfo);
    }
}
