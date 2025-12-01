package io.interfero.database;

import io.interfero.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("it")
@Import(TestcontainersConfiguration.class)
public class DatabaseConnectionIT
{
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcClient jdbcClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry)
    {
        TestcontainersConfiguration.updateContainerProperties(registry);
    }

    @Test
    void shouldHaveDataSourceAndJdbcClient()
    {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcClient).isNotNull();
    }

    @Test
    void shouldHaveInitializedDatabase()
    {
        var result = jdbcClient.sql("""
                SELECT EXISTS (
                    SELECT FROM information_schema.tables
                    WHERE  table_schema = 'public'
                    AND    table_name   = 'databasechangelog'
                );
            """).query().singleValue();

        assertThat(result).isEqualTo(Boolean.TRUE);
    }
}
