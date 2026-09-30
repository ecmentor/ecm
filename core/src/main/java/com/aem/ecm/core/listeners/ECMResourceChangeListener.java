package com.aem.ecm.core.listeners;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(service = ResourceChangeListener.class, immediate = true, property = {
        // 1. Define the paths you want to observe
        ResourceChangeListener.PATHS + "=/content/ecm",

        // 2. Specify the types of resource actions to listen for
        ResourceChangeListener.CHANGES + "=ADDED", ResourceChangeListener.CHANGES + "=CHANGED", ResourceChangeListener.CHANGES + "=REMOVED"})
public class ECMResourceChangeListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(ECMResourceChangeListener.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String SERVICE_USER = "regional-content-reader";

    @Override
    public void onChange(List<ResourceChange> changes) {
        // Iterate through all changes captured in this batch
        for (ResourceChange change : changes) {
            String path = change.getPath();
            ResourceChange.ChangeType changeType = change.getType();

            LOG.info("Resource change detected! Path: {}, Type: {}", path, changeType);

            // Skip resolving if the resource was deleted
            if (ResourceChange.ChangeType.REMOVED.equals(changeType)) {
                LOG.info("Resource at {} was removed.", path);
                continue;
            }

            // Open a session via a Service User to inspect the resource
            Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);

            try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param)) {
                Resource resource = resolver.getResource(path);
                if (resource != null) {
                    LOG.info("Inspecting modified resource. Resource type is: {}", resource.getResourceType());
                    // Execute your business logic here
                }
            } catch (LoginException e) {
                LOG.error("Could not obtain a Service Resource Resolver", e);
            }
        }
    }
}

