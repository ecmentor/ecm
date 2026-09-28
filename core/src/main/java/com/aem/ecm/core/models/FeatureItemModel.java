package com.aem.ecm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FeatureItemModel {

    @ValueMapValue
    private String label;

    @ValueMapValue
    private String icon;


    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }


}
