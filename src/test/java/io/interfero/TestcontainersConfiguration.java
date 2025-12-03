package io.interfero;

import io.github.cdimascio.dotenv.Dotenv;
import io.interfero.database.DatabaseVendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.pulsar.PulsarContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration
{
    private static Dotenv dotenv;
    private static PostgreSQLContainer postgresContainer; // Instance for Postgres and TimescaleDB
    private static final PulsarContainer pulsarClusterAContainer;
    private static final PulsarContainer pulsarClusterBContainer;

    static
    {
        initEnv();

        if (isPostgres())
            initPostgresContainer();
        if (isTimescaledb())
            initTimescaledbContainer();

        pulsarClusterAContainer = createPulsarContainer("cluster-a");
        pulsarClusterBContainer = createPulsarContainer("cluster-b");

        var futures = new ArrayList<CompletableFuture<Void>>();

        futures.add(CompletableFuture.runAsync(postgresContainer::start));
        futures.add(CompletableFuture.runAsync(pulsarClusterAContainer::start));
        futures.add(CompletableFuture.runAsync(pulsarClusterBContainer::start));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
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
    }

    private static void initTimescaledbContainer()
    {
        var timescaledbVersion = dotenv.get("TIMESCALEDB_VERSION");
        log.info("Using TimescaleDB version: {}", timescaledbVersion);

        var image = DockerImageName.parse("timescale/timescaledb:" + timescaledbVersion)
                .asCompatibleSubstituteFor("postgres");
        postgresContainer = new PostgreSQLContainer(image).withDatabaseName("interfero");
    }

    private static PulsarContainer createPulsarContainer(String clusterName)
    {
        var pulsarVersion = dotenv.get("PULSAR_VERSION");
        log.info("Using Pulsar version {} for cluster: {}", pulsarVersion, clusterName);

        var image = DockerImageName.parse("apachepulsar/pulsar-all:" + pulsarVersion);
        return new PulsarContainer(image);
    }

    public static void updateContainerProperties(DynamicPropertyRegistry registry)
    {
        registry.add("interfero.database.url", postgresContainer::getJdbcUrl);
        registry.add("interfero.database.username", postgresContainer::getUsername);
        registry.add("interfero.database.password", postgresContainer::getPassword);

        var databaseVendor = isPostgres() ? DatabaseVendor.postgres : DatabaseVendor.timescaledb;
        registry.add("interfero.database.vendor", databaseVendor::name);

        registry.add("interfero.pulsar.clusters.cluster-a.client.service-url", pulsarClusterAContainer::getPulsarBrokerUrl);
        registry.add("interfero.pulsar.clusters.cluster-a.admin.service-url", pulsarClusterAContainer::getHttpServiceUrl);
        registry.add("interfero.pulsar.clusters.cluster-b.client.service-url", pulsarClusterBContainer::getPulsarBrokerUrl);
        registry.add("interfero.pulsar.clusters.cluster-b.admin.service-url", pulsarClusterBContainer::getHttpServiceUrl);
    }
}