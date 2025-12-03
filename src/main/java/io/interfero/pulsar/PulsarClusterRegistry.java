package io.interfero.pulsar;

import io.interfero.pulsar.config.PulsarConfiguration;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.boot.pulsar.autoconfigure.PulsarProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class PulsarClusterRegistry
{
    private final PulsarConfiguration pulsarConfiguration;
    private final Map<String, PulsarClient> pulsarClients = new HashMap<>();
    private final Map<String, PulsarAdmin> pulsarAdmins = new HashMap<>();

    public PulsarClusterRegistry(PulsarConfiguration pulsarConfiguration)
    {
        this.pulsarConfiguration = pulsarConfiguration;

        initPulsarClientsAndAdmins();
    }

    /**
     * Returns a list of configured Pulsar cluster names. These names must not match the actual cluster names in Pulsar,
     * they are just identifiers from the configuration.
     * @return Set of configured cluster names
     */
    public Set<String> getConfiguredClusterNames()
    {
        return pulsarClients.keySet();
    }

    /**
     * Returns the Pulsar client for the given cluster name.
     * @param clusterName The name of the cluster as defined in the configuration
     * @return Pulsar client for the given cluster name
     * @throws IllegalArgumentException if the cluster name is not found
     */
    public PulsarClient getPulsarClient(String clusterName)
    {
        if (!pulsarClients.containsKey(clusterName))
            throw new IllegalArgumentException("Pulsar cluster not found: " + clusterName);
        return pulsarClients.get(clusterName);
    }

    /**
     * Returns the Pulsar admin for the given cluster name.
     * @param clusterName The name of the cluster as defined in the configuration
     * @return Pulsar admin for the given cluster name
     * @throws IllegalArgumentException if the cluster name is not found
     */
    public PulsarAdmin getPulsarAdmin(String clusterName)
    {
        if (!pulsarAdmins.containsKey(clusterName))
            throw new IllegalArgumentException("Pulsar cluster not found: " + clusterName);
        return pulsarAdmins.get(clusterName);
    }

    private void initPulsarClientsAndAdmins()
    {
        pulsarConfiguration.getClusters().forEach(this::initPulsarCluster);
        pulsarConfiguration.getClusters().forEach(this::initPulsarAdmin);
    }

    private void initPulsarCluster(String clusterName, PulsarProperties properties)
    {
        try
        {
            var client = PulsarClient.builder()
                    .serviceUrl(properties.getClient().getServiceUrl())
                    .build();
            pulsarClients.put(clusterName, client);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create Pulsar client for cluster: " + clusterName, e);
        }
    }

    private void initPulsarAdmin(String clusterName, PulsarProperties properties)
    {
        try
        {
            var admin = PulsarAdmin.builder()
                    .serviceHttpUrl(properties.getAdmin().getServiceUrl())
                    .build();
            pulsarAdmins.put(clusterName, admin);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create Pulsar admin for cluster: " + clusterName, e);
        }
    }
}
