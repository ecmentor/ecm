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
package com.aem.ecm.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.json.Json;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Teaching example of a path-based Sling Servlet that looks up a page by
 * its path and returns the page title.
 *
 * <p>GET /bin/ecm/pagetitle?path=/content/ecm/us/en - returns the title of
 * the given page.</p>
 */
@Component(service = { Servlet.class })
@SlingServletPaths("/bin/ecm/pagetitle")
@ServiceDescription("ECM Training - Page Title Servlet")
public class PageTitleServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException, IOException {
        final String path = request.getParameter("path");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (path == null || path.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Json.createObjectBuilder()
                    .add("error", "Missing required request parameter 'path'")
                    .build().toString());
            return;
        }

        final ResourceResolver resolver = request.getResourceResolver();
        final PageManager pageManager = resolver.adaptTo(PageManager.class);
        final Page page = pageManager != null ? pageManager.getPage(path) : null;

        if (page == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Json.createObjectBuilder()
                    .add("error", "No page found at path '" + path + "'")
                    .build().toString());
            return;
        }

        response.getWriter().write(Json.createObjectBuilder()
                .add("path", path)
                .add("title", page.getTitle() != null ? page.getTitle() : "")
                .build().toString());
    }
}
