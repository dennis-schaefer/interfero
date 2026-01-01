package io.interfero.namespaces;

import io.interfero.clusters.PulsarClusterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.Policies;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NamespaceService
{
    private final PulsarClusterRegistry clusterRegistry;

    public void createNamespaceIfNotExists(String clusterName, String tenant, String namespace,
                                           Policies policies) throws PulsarAdminException
    {
        log.debug("Creating namespace '{}/{}' in cluster '{}' if it does not exist", tenant, namespace, clusterName);
        var pulsarAdmin = clusterRegistry.getPulsarAdmin(clusterName);

        if (namespaceExists(pulsarAdmin, tenant, namespace))
            return;

        pulsarAdmin.namespaces().createNamespace(tenant + "/" + namespace, policies);
        log.info("Created namespace '{}/{}' in cluster '{}'", tenant, namespace, clusterName);
    }

    private boolean namespaceExists(PulsarAdmin pulsarAdmin, String tenant, String namespace) throws PulsarAdminException
    {
        var namespacesInTenant = pulsarAdmin.namespaces().getNamespaces(tenant);
        var exists = namespacesInTenant.contains(tenant + "/" + namespace);

        log.debug("Namespace '{}/{}' existence check: {}", tenant, namespace, exists);
        return exists;
    }
}
