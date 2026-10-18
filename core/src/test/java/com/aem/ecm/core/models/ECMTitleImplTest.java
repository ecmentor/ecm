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

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.aem.ecm.core.testcontext.AppAemContext;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies the Sling Model Delegation Pattern used by {@link ECMTitleImpl}:
 * {@code getText()} is customised to apply an optional "Title Prefix",
 * while every other {@link ECMTitle}/{@code Title} method remains delegated,
 * unchanged, to the Core Components Title model resolved via
 * {@code sling:resourceSuperType} (ecm/components/mytitle -&gt;
 * ecm/components/title -&gt; core/wcm/components/title/v3/title).
 */
@ExtendWith(AemContextExtension.class)
class ECMTitleImplTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Page page;

    @BeforeEach
    void setUp() {
        // Register the resourceSuperType chain in /apps so that
        // @Via(type = ResourceSuperType.class) can resolve the delegate:
        // ecm/components/mytitle -> ecm/components/title -> core Title model.
        context.create().resource("/apps/ecm/components/mytitle",
                "jcr:primaryType", "cq:Component",
                "sling:resourceSuperType", "ecm/components/title");
        context.create().resource("/apps/ecm/components/title",
                "jcr:primaryType", "cq:Component",
                "sling:resourceSuperType", "core/wcm/components/title/v3/title");

        page = context.create().page("/content/mypage");
    }

    /**
     * Creates a mytitle content resource and adapts it (via the request) to
     * {@link ECMTitle}.
     *
     * @param titlePrefix the configured title prefix, or {@code null} if the
     *                     dialog field was never set (property absent)
     * @param titleText   the delegated title's text (stored as {@code jcr:title})
     */
    private ECMTitle createTitle(String titlePrefix, String titleText) {
        List<Object> props = new ArrayList<>();
        props.add("sling:resourceType");
        props.add("ecm/components/mytitle");
        props.add("jcr:title");
        props.add(titleText);
        if (titlePrefix != null) {
            props.add("titlePrefix");
            props.add(titlePrefix);
        }

        Resource resource = context.create().resource(page, "title", props.toArray());

        context.currentPage(page);
        context.currentResource(resource);

        return context.request().adaptTo(ECMTitle.class);
    }

    @Test
    void testTitleWithPrefix() {
        ECMTitle title = createTitle("ECM", "Investment Solutions");

        assertNotNull(title);
        assertEquals("ECM - Investment Solutions", title.getText());
        assertEquals("ECM", title.getTitlePrefix());
    }

    @Test
    void testTitleWithoutPrefix() {
        ECMTitle title = createTitle(null, "Investment Solutions");

        assertNotNull(title);
        assertEquals("Investment Solutions", title.getText());
    }

    @Test
    void testTitleWithBlankPrefix() {
        ECMTitle title = createTitle("   ", "Investment Solutions");

        assertNotNull(title);
        assertEquals("Investment Solutions", title.getText());
    }

    @Test
    void testDelegatedBehaviourRemainsAvailable() {
        ECMTitle title = createTitle("ECM", "Investment Solutions");

        // Not customised by ECMTitleImpl - must still come from the
        // delegated Core Components Title model via resourceSuperType.
        assertNotNull(title.getExportedType());
        assertFalse(title.isLinkDisabled());
    }

}
