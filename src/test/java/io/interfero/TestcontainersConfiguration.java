package io.interfero;

import io.github.cdimascio.dotenv.Dotenv;
import io.interfero.database.DatabaseVendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

@Slf4j
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration
{
    private static Dotenv dotenv;
    private static PostgreSQLContainer postgresContainer; // Instance for Postgres and TimescaleDB

    static
    {
        initEnv();

        if (isPostgres())
            initPostgresContainer();
        if (isTimescaledb())
            initTimescaledbContainer();
    }

    private static void initEnv()
    {
        var envFile = ".testcontainers.env"; // This file is created in GitHub actions and is not committed to the repo
        if (!new File(envFile).exists())
            envFile = ".env"; // As fallback, for running locally

        dotenv =  Dotenv.configure()
                .filename(envFile)
                .load();
    }

    private static boolean isPostgres()
    {
        return DatabaseVendor.postgres.name().equalsIgnoreCase(dotenv.get("INTERFERO_DATABASE_VENDOR"));
    }

    private static boolean isTimescaledb()
    {
        return DatabaseVendor.timescaledb.name().equalsIgnoreCase(dotenv.get("INTERFERO_DATABASE_VENDOR"));
    }

    private static void initPostgresContainer()
    {
        var postgresVersion = dotenv.get("POSTGRES_VERSION");
        log.info("Using Postgres version: {}", postgresVersion);

        var image = DockerImageName.parse("postgres:" + postgresVersion);
        postgresContainer = new PostgreSQLContainer(image).withDatabaseName("interfero");
        postgresContainer.start();
    }

    private static void initTimescaledbContainer()
    {
        var timescaledbVersion = dotenv.get("TIMESCALEDB_VERSION");
        log.info("Using TimescaleDB version: {}", timescaledbVersion);

        var image = DockerImageName.parse("timescale/timescaledb:" + timescaledbVersion)
                .asCompatibleSubstituteFor("postgres");
        postgresContainer = new PostgreSQLContainer(image).withDatabaseName("interfero");
        postgresContainer.start();
    }

    public static void updateContainerProperties(DynamicPropertyRegistry registry)
    {
        registry.add("interfero.database.url", postgresContainer::getJdbcUrl);
        registry.add("interfero.database.username", postgresContainer::getUsername);
        registry.add("interfero.database.password", postgresContainer::getPassword);

        var databaseVendor = isPostgres() ? DatabaseVendor.postgres : DatabaseVendor.timescaledb;
        registry.add("interfero.database.vendor", databaseVendor::name);
    }
}