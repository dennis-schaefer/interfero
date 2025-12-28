package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Postgres-based implementation of the ClusterInfoRepository interface. This is also used if TimescaleDB is selected
 * as database vendor.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "true")
public class ClusterInfoPostgresRepository implements ClusterInfoRepository
{
    private final JdbcClient jdbcClient;

    @Override
    public Set<ClusterInfo> findAll()
    {
        var sql = "SELECT * FROM cluster_info";

        return jdbcClient.sql(sql)
                .query(ClusterInfo.class)
                .set()
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ClusterInfo> findByName(String name)
    {
        var sql = "SELECT * FROM cluster_info WHERE name = :name";

        return jdbcClient.sql(sql)
                .param("name", name)
                .query(ClusterInfo.class)
                .optional();
    }

    @Override
    public ClusterInfo save(ClusterInfo clusterInfo)
    {
        var sql = """
                INSERT INTO cluster_info (name, internal_name, display_name, icon, color)
                VALUES (:name, :internalName, :displayName, :icon, :color)
                ON CONFLICT (name) DO UPDATE SET
                    internal_name = EXCLUDED.internal_name,
                    display_name = EXCLUDED.display_name,
                    icon = EXCLUDED.icon,
                    color = EXCLUDED.color
                """;

        jdbcClient.sql(sql)
                .param("name", clusterInfo.name())
                .param("internalName", clusterInfo.internalName())
                .param("displayName", clusterInfo.displayName())
                .param("icon", clusterInfo.icon())
                .param("color", clusterInfo.color())
                .update();

        return findByName(clusterInfo.name()).orElseThrow();
    }
}
