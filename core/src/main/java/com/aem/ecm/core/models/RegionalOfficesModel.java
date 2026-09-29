package com.aem.ecm.core.models;

import com.aem.ecm.core.services.RegionalOfficeDirectory;
import com.aem.ecm.core.services.RegionalOfficeService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = RegionalOfficesModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class RegionalOfficesModel {

    public static final String RESOURCE_TYPE =
            "ecm/components/ecm-regionaloffice";

    @OSGiService
    private RegionalOfficeDirectory regionalOfficeDirectory;

    public List<RegionalOfficeService> getOffices() {
        if (regionalOfficeDirectory == null) {
            return Collections.emptyList();
        }

        return regionalOfficeDirectory.getAllOffices()
                .stream()
                .sorted(
                        Comparator.comparing(
                                RegionalOfficeService::getOfficeName
                        )
                )
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return getOffices().isEmpty();
    }
}