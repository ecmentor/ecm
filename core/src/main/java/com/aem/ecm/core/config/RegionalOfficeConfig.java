package com.aem.ecm.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "ECM - Regional Office",
        description = "Creates a regional support office configuration"
)
public @interface RegionalOfficeConfig {

    @AttributeDefinition(
            name = "Office Name",
            description = "Display name of the regional office"
    )
    String officeName() default "";

    @AttributeDefinition(
            name = "Country Code",
            description = "Unique country code such as IN, US or UK"
    )
    String countryCode() default "";

    @AttributeDefinition(
            name = "Support Email",
            description = "Support email address for this region"
    )
    String supportEmail() default "";

    @AttributeDefinition(
            name = "Support Phone",
            description = "Support phone number for this region"
    )
    String supportPhone() default "";
}