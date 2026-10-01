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
package com.aem.ecm.core.filters;

import com.aem.ecm.core.config.MaintenanceModeFilterConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Teaching example of a REQUEST-scope Sling Filter that is driven by OSGi
 * configuration. When enabled, it short-circuits the filter chain and
 * returns a {@code 503 Service Unavailable} response for any request whose
 * resource path starts with the configured {@code pathPrefix}, instead of
 * letting the request reach the resolved servlet/component.
 *
 * <p>This demonstrates: request-scope filters, short-circuiting the
 * {@link FilterChain} (not calling {@code doFilter} on the chain), and
 * OSGi {@code @Designate} configuration that can be changed at runtime
 * from the Felix Web Console without redeploying code.</p>
 */
@Component(service = Filter.class,
           property = {
                   EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
           })
@ServiceDescription("ECM Training - Maintenance Mode Filter")
@ServiceRanking(-800)
@ServiceVendor("Adobe")
@Designate(ocd = MaintenanceModeFilterConfig.class)
public class MaintenanceModeFilter implements Filter {

    private volatile boolean enabled;
    private volatile String pathPrefix;

    @Activate
    @Modified
    protected void activate(final MaintenanceModeFilterConfig config) {
        enabled = config.enabled();
        pathPrefix = config.pathPrefix();
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
            final FilterChain filterChain) throws IOException, ServletException {

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;

        if (enabled && pathPrefix != null
                && slingRequest.getRequestPathInfo().getResourcePath().startsWith(pathPrefix)) {
            slingResponse.setStatus(SlingHttpServletResponse.SC_SERVICE_UNAVAILABLE);
            slingResponse.setContentType("text/html");
            slingResponse.setCharacterEncoding("UTF-8");
            slingResponse.getWriter().write(
                    "<html><body><h1>Site under maintenance</h1>"
                            + "<p>Please check back shortly.</p></body></html>");
            // Intentionally not calling filterChain.doFilter(...) - this stops
            // the request from reaching the resolved servlet/component.
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
