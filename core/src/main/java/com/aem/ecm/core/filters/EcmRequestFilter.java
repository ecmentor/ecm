package com.aem.ecm.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.aem.ecm.core.config.EcmFilterConfiguration;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = Filter.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "="
                        + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_PATTERN + "=/content/ecm/.*"
        }
)
@ServiceDescription("Adds ECM processing header to /content/ecm requests")
@ServiceRanking(-700)
public class EcmRequestFilter implements Filter {

    private static final Logger LOG =
            LoggerFactory.getLogger(EcmRequestFilter.class);

    private volatile String processedBy;


    @Activate
    @Modified
    protected void activate(EcmFilterConfiguration config) {

        processedBy = config.processedBy();

        LOG.info("ECM filter activated with processedBy={}", processedBy);
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {

        SlingHttpServletRequest slingRequest =
                (SlingHttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        LOG.debug("Processing ECM request: {}",
                slingRequest.getRequestPathInfo().getResourcePath());

            httpResponse.setHeader(
                    "X-ECM-Processed-By",
                    processedBy
            );

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required
    }

    @Override
    public void destroy() {
        // No cleanup required
    }
}

