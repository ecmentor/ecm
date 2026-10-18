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

import com.adobe.cq.wcm.core.components.models.Title;

/**
 * Extends the Core Components {@link Title} model with an optional
 * "Title Prefix" that is prepended to the delegated title text, e.g.
 * {@code "ECM - Investment Solutions"}.
 *
 * All other behaviour (link, type, id, data layer, etc.) is inherited
 * unchanged from the delegated Core Components {@link Title} model.
 */
public interface ECMTitle extends Title {

    /**
     * Returns the optional prefix configured on the component, which is
     * prepended to the delegated title text by {@link #getText()}.
     *
     * @return the configured title prefix, or {@code null}/blank if not set
     */
    String getTitlePrefix();

}
