package com.aem.ecm.core.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Regional Offices", description = "Regional office with CA CONFIGS")

public @interface CARegionalConfig {

        @Property(label = "Country Code", description = "REGIONAL OFFICE COUNTRY CODE")
        String countryCode() default "";

        @Property(label = "Office Name", description = "REGIONAL OFFICE NAME")
        String officeName() default "";

        @Property(label = "Support EmailL", description = "REGIONAL OFFICE SUPPORT EMAIL")
        String supportEmail() default "";

        @Property(label = "Support Phone", description = "REGIONAL OFFICE SUPPORT CONTACT INFO")
        String supportPhone() default "";

}
