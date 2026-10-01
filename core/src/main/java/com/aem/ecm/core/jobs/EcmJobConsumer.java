package com.aem.ecm.core.jobs;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Component(
        service = JobConsumer.class,
        immediate = true,
        property = {
                // Tie this background service strictly to your topic constant
                JobConsumer.PROPERTY_TOPICS + "=" + JobConstants.ECM_PROCESSING_TOPIC
        }
)
public class EcmJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(EcmJobConsumer.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String SERVICE_USER = "regional-content-reader";

    @Override
    public JobResult process(Job job) {
        // 1. Extract the properties passed from the producer
        String resourcePath = job.getProperty(JobConstants.PROPERTY_RESOURCE_PATH, String.class);
        LOG.info("Processing background Sling Job for resource: {}", resourcePath);

        if (resourcePath == null) {
            // Cancel immediately if data payload is missing to avoid unneeded retry loops
            LOG.error("Job cancelled: Missing required resource path property.");
            return JobResult.CANCEL;
        }

        // 2. Open a session via a Service User to perform heavy backend tasks
        Map<String, Object> param = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE, SERVICE_USER
        );

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param)) {
            Resource resource = resolver.getResource(resourcePath);

            if (resource != null) {
                // RUN HEAVY PROCESSING LOGIC HERE
                // (e.g., Sending metadata to third-party endpoints, calculating image statistics, bulk updates)
                LOG.info("Heavy logic successfully executed on resource: {}", resource.getPath());

                // Return OK to signal that the task completed cleanly and can be cleared from /var/eventing
                return JobResult.OK;
            } else {
                LOG.warn("Target resource not available at this moment: {}", resourcePath);
                // Return FAILED to trigger an automatic retry based on your OSGi Queue configuration
                return JobResult.FAILED;
            }

        } catch (LoginException e) {
            LOG.error("Login Exception while obtaining service resource resolver for background task", e);
            // Retries are helpful if the session provider fluctuates under heavy global load
            return JobResult.FAILED;
        } catch (Exception e) {
            LOG.error("Unexpected error executing job for path: {}", resourcePath, e);
            return JobResult.CANCEL; // Use CANCEL if the error is unrecoverable (e.g. data corruption)
        }
    }
}
