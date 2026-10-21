package com.aem.ecm.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.drew.lang.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ECMMyTeaser.class,
        resourceType = ECMMyTeaserImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ECMMyTeaserImpl implements ECMMyTeaser{

    static final String RESOURCE_TYPE = "ecm/components/myteaser";

    @Self
    @Via(type = ResourceSuperType.class)
    private Teaser delegate;

    @ValueMapValue
    private String campaignLabel;


    @Override
    public String getCampaignLabel() {
        return campaignLabel;
    }

    @Override
    public String getTitle() {
        String originalText = delegate.getTitle();
        if (StringUtils.isBlank(campaignLabel)) {
            return originalText;
        }
        return  " [ " + campaignLabel + " ] " + originalText;
    }

    @Override
    public boolean isActionsEnabled() {
        return delegate.isActionsEnabled();
    }

    @Override
    public List<ListItem> getActions() {
        return delegate.getActions();
    }

    @Override
    public @Nullable Link getLink() {
        return delegate.getLink();
    }

    @Override
    public String getLinkURL() {
        return delegate.getLinkURL();
    }

    @Override
    public Resource getImageResource() {
        return delegate.getImageResource();
    }

    @Override
    public boolean isImageLinkHidden() {
        return delegate.isImageLinkHidden();
    }

    @Override
    public String getPretitle() {
        return delegate.getPretitle();
    }

    @Override
    public boolean isTitleLinkHidden() {
        return delegate.isTitleLinkHidden();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String getTitleType() {
        return delegate.getTitleType();
    }
}
