package com.aem.ecm.core.listeners;

import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

@Component(
        service = EventListener.class,
        immediate = true // Crucial so the listener hooks into the JCR immediately on deployment
)
public class ECMJCREventListener implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ECMJCREventListener.class);

    @Reference
    private SlingRepository repository;

    private Session session;
    private ObservationManager observationManager;

    // Replace with your actual configured subservice name mapped in Service User Mapper
    private static final String SERVICE_USER = "regional-content-reader";

    @Activate
    protected void activate() {
        try {
            // 1. Establish a JCR Session via your Service User configuration
            session = repository.loginService(SERVICE_USER, null);

            // 2. Obtain the ObservationManager from the workspace configuration
            observationManager = session.getWorkspace().getObservationManager();

            // 3. Set bitmask filters targeting only property modifications
            int eventTypes = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;

            String absPath = "/content/ecm";
            boolean isDeep = true;        // Listen to all nested subnodes underneath /content/ecm
            String[] uuid = null;         // Do not filter by specific node UUIDs
            String[] nodeTypeName = null; // Do not restrict node types (processes all elements)
            boolean noLocal = false;      // Handle events triggered by both local operations and others

            // 4. Register the EventListener
            observationManager.addEventListener(
                    this,
                    eventTypes,
                    absPath,
                    isDeep,
                    uuid,
                    nodeTypeName,
                    noLocal
            );

            LOG.info("Successfully registered JCR Property EventListener for path: {}", absPath);

        } catch (RepositoryException e) {
            LOG.error("Repository Exception occurred while trying to register JCR EventListener", e);
        }
    }

    @Deactivate
    protected void deactivate() {
        try {
            // 5. Unregister the event listener to clean up references
            if (observationManager != null) {
                observationManager.removeEventListener(this);
                LOG.info("JCR Property EventListener successfully unregistered.");
            }
        } catch (RepositoryException e) {
            LOG.error("Error while unregistering JCR EventListener", e);
        } finally {
            // 6. Mandatory session cleanup to prevent thread/memory leaks
            if (session != null && session.isLive()) {
                session.logout();
                LOG.info("JCR Session cleanly logged out.");
            }
        }
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        // 7. Iterate through asynchronous event queues dispatched by Oak
        while (eventIterator.hasNext()) {
            try {
                Event event = eventIterator.nextEvent();
                String path = event.getPath();
                int type = event.getType();

                // Differentiate logic based on the specific type of property action
                switch (type) {
                    case Event.PROPERTY_ADDED:
                        LOG.info("Property added under target path: {}", path);
                        break;
                    case Event.PROPERTY_CHANGED:
                        LOG.info("Property modified under target path: {}", path);
                        break;
                    case Event.PROPERTY_REMOVED:
                        LOG.info("Property deleted under target path: {}", path);
                        break;
                    default:
                        break;
                }

                // Node tracking logic can be safely executed here...

            } catch (RepositoryException e) {
                LOG.error("Failed to read properties of individual JCR Event object", e);
            }
        }
    }
}

