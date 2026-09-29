package com.aem.ecm.core.services.impl;

import com.aem.ecm.core.config.RegionalOfficeConfig;
import com.aem.ecm.core.services.RegionalOfficeService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = RegionalOfficeService.class,
        immediate = true,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
        ocd = RegionalOfficeConfig.class,
        factory = true
)
public class RegionalOfficeServiceImpl
        implements RegionalOfficeService {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalOfficeServiceImpl.class);

    private volatile String officeName;
    private volatile String countryCode;
    private volatile String supportEmail;
    private volatile String supportPhone;

    @Activate
    @Modified
    protected void activate(RegionalOfficeConfig config) {
        officeName = config.officeName();
        countryCode = config.countryCode();
        supportEmail = config.supportEmail();
        supportPhone = config.supportPhone();

        LOG.info(
                "Regional office activated: name={}, countryCode={}",
                officeName,
                countryCode
        );
    }

    @Override
    public String getOfficeName() {
        return officeName;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getSupportEmail() {
        return supportEmail;
    }

    @Override
    public String getSupportPhone() {
        return supportPhone;
    }
}