package io.interfero.database;

import jakarta.annotation.PostConstruct;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "interfero.database.enabled", havingValue = "true")
@Import({
        DataSourceTransactionManagerAutoConfiguration.class,
})
class DatabaseConfiguration
{
    @Value("${interfero.database.vendor}")
    private DatabaseVendor databaseVendor;

    @Bean
    @ConfigurationProperties("interfero.database")
    DataSourceProperties dataSourceProperties()
    {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("interfero.database.hikari")
    DataSource dataSource(DataSourceProperties dataSourceProperties)
    {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    JdbcClient jdbcClient(DataSource dataSource)
    {
        return JdbcClient.create(dataSource);
    }

    @Bean
    SpringLiquibase liquibase(DataSource dataSource)
    {
        var liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(databaseVendor.getMasterChangelogPath());
        return liquibase;
    }

    @PostConstruct
    public void printDatabaseConfig()
    {
        log.debug("Database is configured with vendor: {}", databaseVendor);
    }
}
