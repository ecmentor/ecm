package com.aem.ecm.core.utility;

import java.util.Collections;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ResourceResolverUtil.class)
public class ResourceResolverUtilImpl implements ResourceResolverUtil {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getServiceResourceResolver(String subServiceName) throws LoginException {

        Map<String, Object> authenticationInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, subServiceName);

        return resourceResolverFactory.getServiceResourceResolver(authenticationInfo);
    }
}