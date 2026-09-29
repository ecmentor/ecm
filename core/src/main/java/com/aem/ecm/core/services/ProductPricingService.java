package com.aem.ecm.core.services;


public interface ProductPricingService {

    double getDiscountedPrice(double originalPrice);

    double getTaxAmount(double originalPrice);

    double getFinalPrice(double originalPrice);

    double getDiscountPercentage();

    double getTaxPercentage();
}