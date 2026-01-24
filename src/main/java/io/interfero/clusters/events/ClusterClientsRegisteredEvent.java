package io.interfero.clusters.events;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;

public record ClusterClientsRegisteredEvent(String clusterId,
                                            PulsarClient pulsarClient,
                                            PulsarAdmin pulsarAdmin)
{
}
