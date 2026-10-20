package com.aem.ecm.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "ECM Request Filter Configuration",
        description = "Configuration for the ECM Sling request filter"
)
public @interface EcmFilterConfiguration {

    @AttributeDefinition(
            name = "Processed By Header Value",
            description = "Value returned in the X-ECM-Processed-By response header"
    )
    String processedBy() default "ECM-Sling-Filter";

}
