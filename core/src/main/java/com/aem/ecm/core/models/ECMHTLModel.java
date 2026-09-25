package com.aem.ecm.core.models;

import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(adaptables = Resource.class,
        adapters = ECMHTLModel.class,
        resourceType = "ecm/components/ecm-htl", // Binds it directly to your component path
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ECMHTLModel {

    // Maps directly to name="./title"
    @ValueMapValue
    private String title;

    // Maps directly to name="./showFeatures"
    @ValueMapValue
    private boolean showFeatures;

    // Maps directly to name="./titleType"
    @ValueMapValue
    private String titleType;

    // Maps down into the child resource array named="./links"
    // transforms JCR nodes directly into a List of your Java objects
    @ChildResource(name = "links")
    private List<LinkItem> links;

    @PostConstruct
    protected void init() {
        // Set default titleType if not provided
        if (titleType == null || titleType.isEmpty()) {
            titleType = "h2";
        }
    }

    public String getTitle() {
        return title;
    }

    public boolean isShowFeatures() {
        return showFeatures;
    }

    public String getTitleType() {
        return titleType;
    }

    public List<LinkItem> getLinks() {
        return links;
    }
}

