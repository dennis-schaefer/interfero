package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterInfoRecord;
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
    public Set<ClusterInfoRecord> findAll()
    {
        var sql = "SELECT * FROM cluster_info";

        return jdbcClient.sql(sql)
                .query(ClusterInfoRecord.class)
                .set()
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ClusterInfoRecord> findByName(String name)
    {
        var sql = "SELECT * FROM cluster_info WHERE name = :name";

        return jdbcClient.sql(sql)
                .param("name", name)
                .query(ClusterInfoRecord.class)
                .optional();
    }

    @Override
    public ClusterInfoRecord save(ClusterInfoRecord clusterInfoRecord)
    {
        var sql = """
                INSERT INTO cluster_info (name, display_name, icon, color)
                VALUES (:name, :displayName, :icon, :color)
                ON CONFLICT (name) DO UPDATE SET
                    display_name = EXCLUDED.display_name,
                    icon = EXCLUDED.icon,
                    color = EXCLUDED.color
                """;

        jdbcClient.sql(sql)
                .param("name", clusterInfoRecord.name())
                .param("displayName", clusterInfoRecord.displayName())
                .param("icon", clusterInfoRecord.icon())
                .param("color", clusterInfoRecord.color())
                .update();

        return findByName(clusterInfoRecord.name()).orElseThrow();
    }

    @Override
    public void deleteAll()
    {
        var sql = "DELETE FROM cluster_info";
        jdbcClient.sql(sql).update();
    }
}
