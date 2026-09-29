package com.aem.ecm.core.services;

public interface DiscountService {
    double calculateDiscountedPrice(double originalPrice);

    double getDiscountPercentage();
}
