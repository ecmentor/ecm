package com.aem.ecm.core.jobs;

public final class JobConstants {
    // A unique identifier string for your background job
    public static final String ECM_PROCESSING_TOPIC = "com/ecm/core/jobs/ecmprocessing";

    // Property key used to pass data from the listener to the consumer
    public static final String PROPERTY_RESOURCE_PATH = "resourcePath";
}
