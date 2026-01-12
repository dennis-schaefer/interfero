package io.interfero.clusters.repositories;

import io.interfero.clusters.domain.ClusterConnectionSettingsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JDBC-based implementation of {@link ClusterConnectionSettingsRepository}. Currently, this implementation only
 * supports thePostgres SQL dialect.
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "interfero.database.enabled", havingValue = "true")
public class ClusterConnectionSettingsJdbcRepository implements ClusterConnectionSettingsRepository
{
    private final JdbcClient jdbcClient;

    @Override
    public Set<ClusterConnectionSettingsEntity> findAll()
    {
        var sql = "SELECT * FROM cluster_connection_settings";

        return jdbcClient.sql(sql)
                .query(ClusterConnectionSettingsEntity.class)
                .set()
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ClusterConnectionSettingsEntity> findById(Long id)
    {
        var sql = "SELECT * FROM cluster_connection_settings WHERE id = :id";

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(ClusterConnectionSettingsEntity.class)
                .optional();
    }

    @Override
    public ClusterConnectionSettingsEntity save(ClusterConnectionSettingsEntity clusterConnectionSettings)
    {
        Long id = null;
        if (clusterConnectionSettings.id() != null && findById(clusterConnectionSettings.id()).isPresent())
            id = clusterConnectionSettings.id();

        if (id != null)
            return update(id, clusterConnectionSettings);

        return insert(clusterConnectionSettings);
    }

    private ClusterConnectionSettingsEntity update(Long id, ClusterConnectionSettingsEntity clusterConnectionSettings)
    {
        var sql = """
                UPDATE cluster_connection_settings
                SET service_url = :serviceUrl,
                    authentication_method = :authenticationMethod,
                    authentication_details = :authenticationDetails
                WHERE id = :id
                """;

        jdbcClient.sql(sql)
                .param("id", clusterConnectionSettings.id())
                .param("serviceUrl", clusterConnectionSettings.serviceUrl())
                .param("authenticationMethod", clusterConnectionSettings.authenticationMethod().name())
                .param("authenticationDetails", clusterConnectionSettings.authenticationDetails())
                .update();

        return findById(id).orElseThrow();
    }

    private ClusterConnectionSettingsEntity insert(ClusterConnectionSettingsEntity clusterConnectionSettings)
    {
        var keyHolder = new GeneratedKeyHolder();
        var sql = """
                INSERT INTO cluster_connection_settings (service_url, authentication_method, authentication_details)
                VALUES (:serviceUrl, :authenticationMethod, :authenticationDetails)
                """;

        jdbcClient.sql(sql)
                .param("serviceUrl", clusterConnectionSettings.serviceUrl())
                .param("authenticationMethod", clusterConnectionSettings.authenticationMethod().name())
                .param("authenticationDetails", clusterConnectionSettings.authenticationDetails())
                .update(keyHolder, "id");

        var savedId = keyHolder.getKeyAs(Long.class);
        if (savedId == null)
            throw new IllegalStateException("Failed to retrieve generated ID after insert");

        return findById(savedId).orElseThrow();
    }

    @Override
    public void deleteById(Long id)
    {
        var sql = "DELETE FROM cluster_connection_settings WHERE id = :id";

        jdbcClient.sql(sql)
                .param("id", id)
                .update();
    }

    @Override
    public void deleteAll()
    {
        var sql = "DELETE FROM cluster_connection_settings WHERE id > 0";
        jdbcClient.sql(sql).update();
    }
}
