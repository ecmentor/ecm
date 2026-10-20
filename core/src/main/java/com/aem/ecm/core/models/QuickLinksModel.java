package com.aem.ecm.core.models;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(
        adaptables = Resource.class,
        adapters = QuickLinksModel.class,
        resourceType = "ecm/components/ecm-quicklinks",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class QuickLinksModel {

    private final Logger logger = LoggerFactory.getLogger(QuickLinksModel.class);
    @ValueMapValue
    private String heading;

    @ChildResource(name = "links")
    private List<QuickLink> links;

    @PostConstruct
    protected void init() {
        if (links == null || links.isEmpty()) {
            logger.info("QuickLinks: links is null or empty");
        }
        for (QuickLink link : links) {
            if (link != null) {
                logger.info(
                        "QuickLink - label: {}, url: {}, external: {}, target: {}",
                        link.getLabel(),
                        link.getUrl(),
                        link.isExternal(),
                        link.getTarget()
                );
            }
        }

    }

    public String getHeading() {
        return heading != null && !heading.isBlank()
                ? heading
                : "Quick Links";
    }

    public List<QuickLink> getLinks() {
        return links != null ? links : Collections.emptyList();
    }

    public boolean hasLinks() {
        return links != null && !links.isEmpty();
    }
}
