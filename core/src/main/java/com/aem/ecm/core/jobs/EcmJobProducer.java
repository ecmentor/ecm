package com.aem.ecm.core.jobs;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        service = ResourceChangeListener.class,
        immediate = true,
        property = {
                ResourceChangeListener.PATHS + "=/content/ecm",
                ResourceChangeListener.CHANGES + "=ADDED",
                ResourceChangeListener.CHANGES + "=CHANGED"
        }
)
public class EcmJobProducer implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(EcmJobProducer.class);

    @Reference
    private JobManager jobManager;

    @Override
    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            String path = change.getPath();
            LOG.debug("ResourceChange detected at path: {}. Preparing background job...", path);

            // 1. Package the necessary metadata into a standard Java Map
            Map<String, Object> props = new HashMap<>();
            props.put(JobConstants.PROPERTY_RESOURCE_PATH, path);

            // 2. Offload the task immediately to the repository-backed JobManager
            // This returns quickly, keeping user thread responsive
            jobManager.addJob(JobConstants.ECM_PROCESSING_TOPIC, props);

            LOG.info("Sling Job successfully queued for path: {}", path);
        }
    }
}
