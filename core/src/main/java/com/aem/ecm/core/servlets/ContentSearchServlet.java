package com.aem.ecm.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

    @Component(
            service = { Servlet.class },
            property = {
                    "sling.servlet.methods=GET"
            }
    )
    @SlingServletPaths("/bin/ecm/contentsearch")
    public class ContentSearchServlet extends SlingSafeMethodsServlet {

        private static final long serialVersionUID = 1L;

        @Reference
        private QueryBuilder queryBuilder;

        @Override
        protected void doGet(
                SlingHttpServletRequest request,
                SlingHttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String category = request.getParameter("category");

            if (category == null || category.trim().isEmpty()) {
                response.setStatus(
                        SlingHttpServletResponse.SC_BAD_REQUEST
                );
                writeError(response, "Missing required parameter: category");
                return;
            }

            int limit;
            int offset;

            try {
                limit = parseInteger(
                        request.getParameter("limit"), -1
                );
                offset = parseInteger(
                        request.getParameter("offset"), 0
                );

                if ((limit != -1 && limit <= 0) || offset < 0) {
                    throw new IllegalArgumentException(
                            "limit must be -1 or positive; offset must be non-negative"
                    );
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(
                        SlingHttpServletResponse.SC_BAD_REQUEST
                );
                writeError(response, e.getMessage());
                return;
            }

            Map<String, String> predicates = new HashMap<>();
            predicates.put("path", "/content/ecm");
            predicates.put("type", "cq:Page");
            predicates.put(
                    "property", "jcr:content/contentCategory"
            );
            predicates.put("property.value", category);
            predicates.put("p.limit", String.valueOf(limit));
            predicates.put("p.offset", String.valueOf(offset));

            ResourceResolver resolver = request.getResourceResolver();
            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                response.setStatus(
                        SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                writeError(response, "Unable to obtain JCR session");
                return;
            }

            JSONArray results = new JSONArray();

            try {
                Query query = queryBuilder.createQuery(
                        PredicateGroup.create(predicates), session
                );

                SearchResult searchResult = query.getResult();

                for (Hit hit : searchResult.getHits()) {
                    String pagePath = hit.getPath();

                    Resource contentResource =
                            resolver.getResource(
                                    pagePath + "/jcr:content"
                            );

                    String title = pagePath;

                    if (contentResource != null) {
                        ValueMap properties =
                                contentResource.getValueMap();

                        title = properties.get(
                                "jcr:title", pagePath
                        );
                    }

                    JSONObject pageJson = new JSONObject();
                    pageJson.put("title", title);
                    pageJson.put("path", pagePath);
                    results.put(pageJson);
                }

                JSONObject output = new JSONObject();
                output.put("category", category);
                output.put("limit", limit);
                output.put("offset", offset);
                output.put("results", results);

                response.setStatus(
                        SlingHttpServletResponse.SC_OK
                );
                response.getWriter().write(output.toString());

            } catch (Exception e) {
                response.setStatus(
                        SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                writeError(response, "Content search failed");
            }
        }

        private int parseInteger(String value, int defaultValue) {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(value.trim());
        }

        private void writeError(
                SlingHttpServletResponse response, String message)
                throws IOException {

            JSONObject error = new JSONObject();
            error.put("error", message);
            response.getWriter().write(error.toString());
        }
    }
