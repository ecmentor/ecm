package com.aem.ecm.core.services.impl;


import com.aem.ecm.core.services.DiscountService;
import com.aem.ecm.core.services.ProductPricingService;
import com.aem.ecm.core.services.TaxService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = ProductPricingService.class,
        immediate = true
)
public class ProductPricingServiceImpl
        implements ProductPricingService {

    @Reference
    private DiscountService discountService;

    @Reference
    private TaxService taxService;

    @Override
    public double getDiscountedPrice(double originalPrice) {
        return discountService.calculateDiscountedPrice(originalPrice);
    }

    @Override
    public double getTaxAmount(double originalPrice) {
        double discountedPrice = getDiscountedPrice(originalPrice);

        return taxService.calculateTax(discountedPrice);
    }

    @Override
    public double getFinalPrice(double originalPrice) {
        double discountedPrice = getDiscountedPrice(originalPrice);
        double taxAmount = taxService.calculateTax(discountedPrice);

        return discountedPrice + taxAmount;
    }

    @Override
    public double getDiscountPercentage() {
        return discountService.getDiscountPercentage();
    }

    @Override
    public double getTaxPercentage() {
        return taxService.getTaxPercentage();
    }
}