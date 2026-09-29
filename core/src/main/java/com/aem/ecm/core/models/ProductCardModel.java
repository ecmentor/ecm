package com.aem.ecm.core.models;


import com.aem.ecm.core.services.ProductPricingService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;


@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = ProductCardModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ProductCardModel {

    public static final String RESOURCE_TYPE =
            "ecm/components/ecm-productcard";

    @ValueMapValue
    private String productName;

    @ValueMapValue
    private double price;

    @OSGiService
    private ProductPricingService productPricingService;

    @PostConstruct
    protected void init() {
        // Set default titleType if not provided
        if (productName == null || productName.isEmpty()) {
            productName = "Product";
        }
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscountPercentage() {
        return productPricingService != null
                ? productPricingService.getDiscountPercentage()
                : 0;
    }

    public double getDiscountedPrice() {
        return productPricingService != null
                ? productPricingService.getDiscountedPrice(price)
                : price;
    }

    public double getTaxPercentage() {
        return productPricingService != null
                ? productPricingService.getTaxPercentage()
                : 0;
    }

    public double getTaxAmount() {
        return productPricingService != null
                ? productPricingService.getTaxAmount(price)
                : 0;
    }

    public double getFinalPrice() {
        return productPricingService != null
                ? productPricingService.getFinalPrice(price)
                : price;
    }
}