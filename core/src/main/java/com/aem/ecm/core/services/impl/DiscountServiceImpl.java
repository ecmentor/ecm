package com.aem.ecm.core.services.impl;

import com.aem.ecm.core.config.DiscountServiceConfig;
import com.aem.ecm.core.services.DiscountService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;


@Component(
        service = DiscountService.class,
        immediate = true
)
@Designate(ocd = DiscountServiceConfig.class)
public class DiscountServiceImpl implements DiscountService {

    private volatile double discountPercentage;

    @Activate
    @Modified
    protected void activate(DiscountServiceConfig config) {
        discountPercentage = config.discountPercentage();
    }

    @Override
    public double calculateDiscountedPrice(double originalPrice) {
        double discountAmount =
                originalPrice * discountPercentage / 100;

        return originalPrice - discountAmount;
    }

    @Override
    public double getDiscountPercentage() {
        return discountPercentage;
    }
}