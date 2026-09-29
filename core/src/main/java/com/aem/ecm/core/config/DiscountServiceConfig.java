package com.aem.ecm.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "ECM - Discount Service Configuration",
        description = "Configuration for the product discount service"
)
public @interface DiscountServiceConfig {

    @AttributeDefinition(
            name = "Discount Percentage",
            description = "Percentage discount applied to the original price"
    )
    double discountPercentage() default 10.0;

}