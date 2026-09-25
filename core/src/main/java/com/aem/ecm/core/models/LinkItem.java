package com.aem.ecm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkItem {

    // Maps directly to name="label" in the dialog multifield container
    @ValueMapValue
    private String label;

    // Maps directly to name="url" in the dialog multifield container
    @ValueMapValue
    private String url;

    // Maps directly to name="isExternal" in the dialog multifield container
    @ValueMapValue
    private boolean isExternal;

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public boolean isExternal() {
        return isExternal;
    }
}

