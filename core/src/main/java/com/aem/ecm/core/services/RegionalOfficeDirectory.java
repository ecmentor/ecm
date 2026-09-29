package com.aem.ecm.core.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(
        service = RegionalOfficeDirectory.class,
        immediate = true
)
public class RegionalOfficeDirectory {

    private final List<RegionalOfficeService> regionalOffices =
            new CopyOnWriteArrayList<>();

    @Reference(
            service = RegionalOfficeService.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    protected void bindRegionalOffice(
            RegionalOfficeService regionalOffice) {

        regionalOffices.add(regionalOffice);
    }

    protected void unbindRegionalOffice(
            RegionalOfficeService regionalOffice) {

        regionalOffices.remove(regionalOffice);
    }

    public List<RegionalOfficeService> getAllOffices() {
        return Collections.unmodifiableList(
                new ArrayList<>(regionalOffices)
        );
    }

    public RegionalOfficeService getOfficeByCountryCode(
            String countryCode) {

        if (countryCode == null) {
            return null;
        }

        return regionalOffices.stream()
                .filter(office ->
                        countryCode.equalsIgnoreCase(
                                office.getCountryCode()
                        )
                )
                .findFirst()
                .orElse(null);
    }
}