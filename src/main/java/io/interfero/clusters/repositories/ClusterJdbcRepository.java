package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterEntity;
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
 * JDBC-based implementation of the {@link ClusterRepository}. Currently, this implementation only supports the Postgres
 * SQL dialect.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "true")
public class ClusterJdbcRepository implements ClusterRepository
{
    private final JdbcClient jdbcClient;

    @Override
    public Set<ClusterEntity> findAll()
    {
        var sql = "SELECT * FROM cluster";

        return jdbcClient.sql(sql)
                .query(ClusterEntity.class)
                .set()
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ClusterEntity> findById(String id)
    {
        var sql = "SELECT * FROM cluster WHERE id = :id";

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(ClusterEntity.class)
                .optional();
    }

    @Override
    public ClusterEntity save(ClusterEntity clusterEntity)
    {
        var sql = """
                INSERT INTO cluster (id, display_name, icon, color, client_connection_settings_id, admin_connection_settings_id)
                VALUES (:id, :displayName, :icon, :color, :clientConnectionSettingsId, :adminConnectionSettingsId)
                ON CONFLICT (id) DO UPDATE SET
                    display_name = EXCLUDED.display_name,
                    icon = EXCLUDED.icon,
                    color = EXCLUDED.color,
                    client_connection_settings_id = EXCLUDED.client_connection_settings_id,
                    admin_connection_settings_id = EXCLUDED.admin_connection_settings_id
                """;

        jdbcClient.sql(sql)
                .param("id", clusterEntity.getId())
                .param("displayName", clusterEntity.getDisplayName())
                .param("icon", clusterEntity.getIcon())
                .param("color", clusterEntity.getColor())
                .param("clientConnectionSettingsId", clusterEntity.getClientConnectionSettingsId())
                .param("adminConnectionSettingsId", clusterEntity.getAdminConnectionSettingsId())
                .update();

        return findById(clusterEntity.getId()).orElseThrow();
    }

    @Override
    public void deleteById(String id)
    {
        var sql = "DELETE FROM cluster WHERE id = :id";

        jdbcClient.sql(sql)
                .param("id", id)
                .update();
    }

    @Override
    public void deleteAll()
    {
        var sql = "DELETE FROM cluster";
        jdbcClient.sql(sql).update();
    }
}
