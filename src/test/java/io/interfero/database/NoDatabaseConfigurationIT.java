package io.interfero.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"it", "db-disabled"})
public class NoDatabaseConfigurationIT
{
    @Autowired(required = false)
    private DataSource noDataSource;

    @Autowired(required = false)
    private JdbcClient noJdbcClient;

    @Autowired
    private DisabledDatabaseWarning disabledDatabaseWarning;

    @Test
    void shouldHaveNoDataSourceAndJdbcClient()
    {
        assertThat(noDataSource).isNull();
        assertThat(noJdbcClient).isNull();
    }

    @Test
    void shouldHaveDisabledDatabaseWarning()
    {
        assertThat(disabledDatabaseWarning).isNotNull();
    }
}
