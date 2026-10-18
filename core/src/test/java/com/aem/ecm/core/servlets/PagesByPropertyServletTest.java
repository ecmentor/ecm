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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class PagesByPropertyServletTest {

    private final PagesByPropertyServlet fixture = new PagesByPropertyServlet();

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit;

    @Mock
    private Session session;

    @Test
    void doGetReturnsMatchingPages(AemContext context) throws ServletException, IOException, RepositoryException {
        context.build()
                .resource("/content/ecm/lucene-lab-2/jcr:content", "jcr:title", "Lucene Lab 2 - Business Unit Demo")
                .commit();

        when(hit.getPath()).thenReturn("/content/ecm/lucene-lab-2/jcr:content");
        final ValueMap hitProperties = context.resourceResolver()
                .getResource("/content/ecm/lucene-lab-2/jcr:content").getValueMap();
        when(hit.getProperties()).thenReturn(hitProperties);
        when(searchResult.getHits()).thenReturn(List.of(hit));
        when(query.getResult()).thenReturn(searchResult);
        when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);

        context.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder);
        context.registerAdapter(ResourceResolver.class, Session.class, session);

        final MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(java.util.Map.of(
                "property", "businessUnit",
                "value", "retail-banking"));
        final MockSlingHttpServletResponse response = context.response();

        fixture.doGet(request, response);

        final String output = response.getOutputAsString();
        assertEquals(200, response.getStatus());
        assertTrue(output.contains("\"total\":1"));
        assertTrue(output.contains("/content/ecm/lucene-lab-2"));
        assertTrue(output.contains("Lucene Lab 2 - Business Unit Demo"));
    }

    @Test
    void doGetReturnsBadRequestWhenPropertyMissing(AemContext context) throws ServletException, IOException {
        final MockSlingHttpServletRequest request = context.request();
        final MockSlingHttpServletResponse response = context.response();

        fixture.doGet(request, response);

        assertEquals(400, response.getStatus());
        assertTrue(response.getOutputAsString().contains("error"));
    }
}
