package com.aem.ecm.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = ECMTeaser.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ECMTeaser {

    public static final String RESOURCE_TYPE =
            "ecm/components/ecm-teaser";

    /*
     * Reads properties from the current component resource.
     */
    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    /*
     * Java field name differs from the JCR property name.
     */
    @ValueMapValue(name = "ctaLink")
    private String configuredCtaLink;

    /*
     * Finds the child resource named "items" and adapts each
     * child such as item0 and item1 to FeatureItemModel.
     */
    @ChildResource(name = "items")
    private List<FeatureItemModel> items;

    /*
     * Because this model is request-adaptable,
     * @Self injects the original SlingHttpServletRequest.
     */
    @Self
    private SlingHttpServletRequest request;

    /*
     * Sling objects derived from the current request.
     */
    @SlingObject
    private Resource resource;


    /*
     * Value supplied when the model is instantiated from HTL.
     */
    @RequestAttribute
    private String theme;

    /*
     * Values supplied through the HTL/Sling bindings.
     */
    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private PageManager pageManager;

    private String campaign;


    @PostConstruct
    private void init() {

        /*
         * There is no standard injector-specific
         * @RequestParameter annotation.
         */
        campaign = request.getParameter("campaign");

        if (theme == null || theme.trim().isEmpty()) {
            theme = "light";
        }

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getConfiguredCtaLink() {
        return configuredCtaLink;
    }


    public List<FeatureItemModel> getItems() {
        return items == null ? Collections.emptyList() : items;
    }

    public String getTheme() {
        return theme;
    }

    public String getCampaign() {
        return campaign;
    }

    public String getResourcePath() {
        return resource != null ? resource.getPath() : "";
    }


    public String getCurrentPageTitle() {
        if (currentPage == null) {
            return "";
        }


        if (currentPage.getTitle() != null) {
            return currentPage.getTitle();
        }

        return currentPage.getName();
    }

    public String getContainingPagePath() {
        if (pageManager == null || resource == null) {
            return "";
        }
        Page containingPage = pageManager.getContainingPage(resource);

        return containingPage != null
                ? containingPage.getPath()
                : "";
    }

}
