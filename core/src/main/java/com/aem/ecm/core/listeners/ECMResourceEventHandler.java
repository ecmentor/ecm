package com.aem.ecm.core.listeners;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.SlingConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Component(service = EventHandler.class, immediate = true, property = {
        // 1. Specify the types of events you want to listen to
        EventConstants.EVENT_TOPIC + "=" + SlingConstants.TOPIC_RESOURCE_ADDED, EventConstants.EVENT_TOPIC + "=" + SlingConstants.TOPIC_RESOURCE_CHANGED,

        // 2. Filter paths so it only runs for your specific target directory
        EventConstants.EVENT_FILTER + "=(path=/content/ecm/*)"})
public class ECMResourceEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ECMResourceEventHandler.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String SERVICE_USER = "regional-content-reader";

    @Override
    public void handleEvent(final Event event) {
        // Extract the path of the modified resource from the event properties
        String resourcePath = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        LOG.info("Event received! Topic: {}, Path: {}", event.getTopic(), resourcePath);

        // Event Handlers do not have a session context, so use a Service User
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param)) {

            Resource resource = resolver.getResource(resourcePath);
            if (resource != null) {
                LOG.info("Successfully resolved resource type: {}", resource.getResourceType());
                // Execute your asynchronous business logic here
            }

        } catch (LoginException e) {
            LOG.error("Login Exception while obtaining service resource resolver", e);
        }
    }
}

