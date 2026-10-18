/*
 *  Copyright 2026 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.ecm.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

/**
 * Sling Model for {@code ecm/components/mytitle}.
 *
 * <p>Demonstrates the <b>Sling Model Delegation Pattern</b>: instead of
 * re-implementing the Core Components {@link Title} model, this class
 * delegates to the model that is resolved for the component's
 * {@code sling:resourceSuperType} chain
 * ({@code ecm/components/mytitle -> ecm/components/title ->
 * core/wcm/components/title/v3/title}) and only customises
 * {@link #getText()}.</p>
 *
 * <p>{@code @Via(type = ResourceSuperType.class)} tells the Sling Models
 * framework to adapt the current resource/request not to this component's
 * own resource type, but to the resource type declared in
 * {@code sling:resourceSuperType}. Combined with {@code @Self}, it resolves
 * (and caches) the delegate instance, which is exactly the {@link Title}
 * model that would have been used had {@code mytitle} not overridden
 * anything - i.e. the Core Title model, resolved through the project's
 * {@code ecm/components/title} proxy.</p>
 *
 * <p>Note: this project does not currently have Lombok configured (see
 * {@code core/pom.xml} and root {@code pom.xml}), so rather than adding a
 * new annotation-processing dependency for a single class, the delegation
 * normally expressed with Lombok's {@code @Delegate(excludes =
 * TitleDelegateExclusion.class)} is implemented here as explicit
 * pass-through methods to {@code delegate}. {@link #getText()} is the only
 * method intentionally NOT forwarded, since it is customised below.</p>
 */
@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ECMTitle.class,
        resourceType = ECMTitleImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ECMTitleImpl implements ECMTitle {

    static final String RESOURCE_TYPE = "ecm/components/mytitle";

    /*
     * Resolves the Title model for the resourceSuperType chain
     * (ecm/components/title -> core/wcm/components/title/v3/title),
     * i.e. the same model that would render this resource if mytitle
     * added no customisation at all.
     */
    @Self
    @Via(type = ResourceSuperType.class)
    private Title delegate;

    /*
     * New dialog field added on mytitle only: ./titlePrefix
     */
    @ValueMapValue
    private String titlePrefix;

    public String getTitlePrefix() {
        return titlePrefix;
    }

    /**
     * Customised: prepends the configured {@code titlePrefix} to the
     * delegated title text, when a prefix is configured.
     */
    @Override
    public String getText() {
        String originalText = delegate.getText();
        if (StringUtils.isBlank(titlePrefix)) {
            return originalText;
        }
        return titlePrefix + " - " + originalText;
    }

    // --- Everything below is forwarded, unmodified, to the delegate. ---
    // Equivalent to Lombok's @Delegate(excludes = TitleDelegateExclusion.class)
    // on the `delegate` field above.

    @Override
    public String getType() {
        return delegate.getType();
    }

    @Override
    public Link getLink() {
        return delegate.getLink();
    }

    @Override
    @Deprecated
    public String getLinkURL() {
        return delegate.getLinkURL();
    }

    @Override
    public boolean isLinkDisabled() {
        return delegate.isLinkDisabled();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public ComponentData getData() {
        return delegate.getData();
    }

    @Override
    public String getAppliedCssClasses() {
        return delegate.getAppliedCssClasses();
    }

    @Override
    public String getExportedType() {
        return delegate.getExportedType();
    }

}
