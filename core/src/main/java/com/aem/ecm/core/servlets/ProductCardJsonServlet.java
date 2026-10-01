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

import com.aem.ecm.core.models.ProductCardModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.json.Json;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Teaching example of a Sling Servlet registered against a resource type
 * rather than a fixed path. It is only invoked for requests to a
 * {@link ProductCardModel#RESOURCE_TYPE} resource that carry the
 * {@code data} selector and {@code json} extension, so it does not
 * interfere with the component's normal HTML rendering.
 *
 * <p>Example request:</p>
 * <pre>GET /content/ecm/us/en/.../jcr:content/root/productcard.data.json</pre>
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes = ProductCardModel.RESOURCE_TYPE,
        methods = HttpConstants.METHOD_GET,
        selectors = "data",
        extensions = "json")
@ServiceDescription("ECM Training - Product Card JSON Export Servlet")
public class ProductCardJsonServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException, IOException {
        final Resource resource = request.getResource();
        final ValueMap properties = resource.getValueMap();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(Json.createObjectBuilder()
                .add("path", resource.getPath())
                .add("productName", properties.get("productName", "Product"))
                .add("price", properties.get("price", 0d))
                .build().toString());
    }
}
