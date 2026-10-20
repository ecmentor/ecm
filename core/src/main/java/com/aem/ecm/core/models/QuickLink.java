package com.aem.ecm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        adapters = QuickLink.class,
        resourceType = "ecm/components/ecm-quicklinks",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class QuickLink {

    @ValueMapValue
    private String label;

    @ValueMapValue
    private String url;

    @ValueMapValue
    private boolean external;

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public boolean isExternal() {
        return external;
    }

    public String getTarget() {
        return external ? "_blank" : "_self";
    }
}
