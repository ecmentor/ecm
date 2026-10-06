package com.aem.ecm.core.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/ecm/products",
                "sling.servlet.methods=GET"
        }
)
public class ProductSearchServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String keyword = request.getParameter("keyword");

        // Validate required parameter
        if (category == null || category.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "category parameter is required"
            );
            return;
        }

        // Mock product data
        List<Product> products = Arrays.asList(
                new Product(
                        "Laptop Pro",
                        "/content/ecm/products/laptop-pro"
                ),
                new Product(
                        "Laptop Air",
                        "/content/ecm/products/laptop-air"
                )
        );

        SearchResponse searchResponse =
                new SearchResponse(category, keyword, products);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(
                response.getWriter(),
                searchResponse
        );
    }

    public static class Product {

        private final String name;
        private final String path;

        public Product(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }

    public static class SearchResponse {

        private final String category;
        private final String keyword;
        private final List<Product> results;

        public SearchResponse(
                String category,
                String keyword,
                List<Product> results) {

            this.category = category;
            this.keyword = keyword;
            this.results = results;
        }

        public String getCategory() {
            return category;
        }

        public String getKeyword() {
            return keyword;
        }

        public List<Product> getResults() {
            return results;
        }
    }
}

