package com.aem.ecm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.aem.ecm.core.config.SocialMediaConfig;

import javax.annotation.PostConstruct;

/**
 * Sling Model that reads the {@link SocialMediaConfig} Context-Aware Configuration
 * for the current resource and exposes the resolved URLs to HTL.
 * <p>
 * The configuration is resolved against the resource's content tree (via its
 * {@code cq:conf} property), falling back to any default values defined higher
 * up the {@code /conf} tree.
 */
@Model(adaptables = Resource.class)
public class SocialMediaLinksModel {

    @SlingObject
    private Resource currentResource;

    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;

    @PostConstruct
    protected void init() {
        SocialMediaConfig config = currentResource.adaptTo(ConfigurationBuilder.class).as(SocialMediaConfig.class);
        facebookUrl = config.facebookUrl();
        twitterUrl = config.twitterUrl();
        linkedinUrl = config.linkedinUrl();
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public boolean isConfigured() {
        return !facebookUrl.isEmpty() || !twitterUrl.isEmpty() || !linkedinUrl.isEmpty();
    }
}
