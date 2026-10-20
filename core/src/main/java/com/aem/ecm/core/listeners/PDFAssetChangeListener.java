package com.aem.ecm.core.listeners;

import java.util.List;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
            service = ResourceChangeListener.class, immediate = true,
            property = {
                    ResourceChangeListener.PATHS + "=/content/dam/ecm",
                    ResourceChangeListener.CHANGES + "=ADDED",
                    ResourceChangeListener.CHANGES + "=CHANGED"
            }
    )

public class PDFAssetChangeListener implements ResourceChangeListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(PDFAssetChangeListener.class);

    @Override
    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            String resourcePath = change.getPath();

            if (resourcePath != null
                    && resourcePath.toLowerCase().endsWith(".pdf")) {

                LOG.info(
                        "[PDF MONITOR] Action: {} detected on PDF asset: {}",
                        change.getType(),
                        resourcePath
                );
            }
        }
    }
}