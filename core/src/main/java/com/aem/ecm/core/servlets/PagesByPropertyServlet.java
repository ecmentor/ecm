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

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Teaching example of a Sling Servlet that wraps the AEM {@link QueryBuilder}
 * API to look up {@code cq:PageContent} nodes by an arbitrary property/value
 * pair, instead of hand-writing JCR-SQL2/XPath.
 *
 * <p>Example request (uses the {@code businessUnit} property and the
 * {@code ecmBusinessUnitLucene-1} Oak index created for the Lucene indexing
 * lab):</p>
 * <pre>
 * GET /bin/ecm/pagesbyproperty?path=/content/ecm&amp;property=businessUnit&amp;value=retail-banking
 * </pre>
 *
 * <p>{@code path} defaults to {@code /content/ecm} if not supplied.</p>
 */
@Component(service = { Servlet.class })
@SlingServletPaths("/bin/ecm/pagesbyproperty")
@ServiceDescription("ECM Training - Pages By Property QueryBuilder Servlet")
public class PagesByPropertyServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_SEARCH_PATH = "/content/ecm";

    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        final String property = request.getParameter("property");
        final String value = request.getParameter("value");

        if (property == null || property.isEmpty() || value == null || value.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(Json.createObjectBuilder()
                    .add("error", "Missing required request parameter 'property' and/or 'value'")
                    .build().toString());
            return;
        }

        final String searchPath = request.getParameter("path") != null
                ? request.getParameter("path") : DEFAULT_SEARCH_PATH;

        final ResourceResolver resolver = request.getResourceResolver();
        final QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        final Session session = resolver.adaptTo(Session.class);

        if (queryBuilder == null || session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Json.createObjectBuilder()
                    .add("error", "QueryBuilder is not available")
                    .build().toString());
            return;
        }

        final Map<String, String> predicates = new HashMap<>();
        predicates.put("path", searchPath);
        predicates.put("type", "cq:PageContent");
        predicates.put("property", property);
        predicates.put("property.value", value);
        predicates.put("p.limit", "-1");

        final Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);
        final SearchResult result = query.getResult();

        final JsonArrayBuilder hitsBuilder = Json.createArrayBuilder();
        final int total;
        try {
            final List<Hit> hits = result.getHits();
            total = hits.size();
            for (final Hit hit : hits) {
                final String contentPath = hit.getPath();
                final String pagePath = contentPath.endsWith("/jcr:content")
                        ? contentPath.substring(0, contentPath.length() - "/jcr:content".length())
                        : contentPath;
                final String title = hit.getProperties().get("jcr:title", String.class);
                hitsBuilder.add(Json.createObjectBuilder()
                        .add("path", pagePath)
                        .add("title", title != null ? title : ""));
            }
        } catch (RepositoryException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Json.createObjectBuilder()
                    .add("error", "Error reading query results: " + e.getMessage())
                    .build().toString());
            return;
        }

        response.getWriter().write(Json.createObjectBuilder()
                .add("path", searchPath)
                .add("property", property)
                .add("value", value)
                .add("total", total)
                .add("results", hitsBuilder)
                .build().toString());
    }
}
