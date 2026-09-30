package com.aem.ecm.core.utility;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolverUtil {

    ResourceResolver getServiceResourceResolver(
            String subServiceName
    ) throws LoginException;
}