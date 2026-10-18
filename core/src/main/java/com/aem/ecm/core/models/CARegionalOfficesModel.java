package com.aem.ecm.core.models;

import com.aem.ecm.core.config.CARegionalConfig;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class)
public class CARegionalOfficesModel {

    @SlingObject
    private Resource currentResource;

        private String countryCode;
        private String officeName;
        private String supportEmail;
        private String supportPhone;

        @PostConstruct
        protected void init() {
        CARegionalConfig config = currentResource.adaptTo(ConfigurationBuilder.class).as(CARegionalConfig.class);
        countryCode = config.countryCode();
        officeName = config.officeName();
        supportEmail = config.supportEmail();
        supportPhone = config.supportPhone();
    }

        public String getCountryCode() {
        return countryCode;
    }

        public String getOfficeName() {
        return officeName;
    }

        public String getSupportEmail() {
        return supportEmail;
    }

        public String getSupportPhone() {
        return supportPhone;
    }

        public boolean isConfigured() {
        return !countryCode.isEmpty() || !officeName.isEmpty() || !supportEmail.isEmpty() || !supportPhone.isEmpty();
    }

}
