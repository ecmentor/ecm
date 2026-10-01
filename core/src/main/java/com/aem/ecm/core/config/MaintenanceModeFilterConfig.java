package com.aem.ecm.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "ECM - Maintenance Mode Filter Configuration",
        description = "Configuration for the maintenance mode request filter"
)
public @interface MaintenanceModeFilterConfig {

    @AttributeDefinition(
            name = "Enabled",
            description = "When enabled, requests under the configured path are blocked with a 503 response"
    )
    boolean enabled() default false;

    @AttributeDefinition(
            name = "Protected Path Prefix",
            description = "Requests whose resource path starts with this prefix are blocked while maintenance mode is enabled"
    )
    String pathPrefix() default "/content/ecm";

}
