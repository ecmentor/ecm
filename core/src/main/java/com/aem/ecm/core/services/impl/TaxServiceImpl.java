package com.aem.ecm.core.services.impl;


import com.aem.ecm.core.services.TaxService;
import org.osgi.service.component.annotations.Component;

@Component(
        service = TaxService.class,
        immediate = true
)
public class TaxServiceImpl implements TaxService {

    private static final double TAX_PERCENTAGE = 18.0;

    @Override
    public double calculateTax(double price) {
        return price * TAX_PERCENTAGE / 100;
    }

    @Override
    public double getTaxPercentage() {
        return TAX_PERCENTAGE;
    }
}