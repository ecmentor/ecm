package com.aem.ecm.core.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Context-Aware Configuration exposing per-site social media links.
 * <p>
 * Unlike OSGi configurations (e.g. {@link RegionalOfficeConfig}), this is resolved
 * per content path via the {@code cq:conf} property on the content tree and is
 * editable by authors through Tools &gt; General &gt; Configuration Browser,
 * without any code deployment.
 * <p>
 * Default values live at {@code /conf/ecm/sling:configs}. A narrower content
 * tree (e.g. a future {@code /content/ecm/uk}) can override just the values it
 * needs by defining its own {@code /conf/ecm/uk/sling:configs} node.
 */
@Configuration(label = "Social Media Links", description = "Per-site social media handles shown in the footer")
public @interface SocialMediaConfig {

    @Property(label = "Facebook URL", description = "Full URL to the Facebook page")
    String facebookUrl() default "";

    @Property(label = "Twitter/X URL", description = "Full URL to the Twitter/X profile")
    String twitterUrl() default "";

    @Property(label = "LinkedIn URL", description = "Full URL to the LinkedIn company page")
    String linkedinUrl() default "";
}
