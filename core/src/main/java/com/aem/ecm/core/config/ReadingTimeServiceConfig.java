package com.aem.ecm.core.config;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "ECM - Reading Service Configuration",
        description = "Configures article reading-time calculations"
)
public  @interface ReadingTimeServiceConfig {
    int wordsPerMinute() default 200;
    int minimumReadingTime() default 1;
    String readingTimeLabel() default "Estimated reading time";
}
