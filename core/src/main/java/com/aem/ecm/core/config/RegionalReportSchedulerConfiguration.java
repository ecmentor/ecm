package com.aem.ecm.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "ECM Regional Content Report Scheduler", description = "Configuration for the regional report Scheduler")
public @interface RegionalReportSchedulerConfiguration {

    @AttributeDefinition(name = "Enabled", description = "Enable or disable the Scheduler") boolean enabled() default true;

    @AttributeDefinition(name = "Scheduler Name", description = "Unique name used to register the scheduled Job") String schedulerName() default "ECM Regional Content Report";

    @AttributeDefinition(name = "Cron Expression", description = "Quartz cron expression controlling execution") String cronExpression() default "0 * * * * ?";

    @AttributeDefinition(name = "Region", description = "Region included in the generated report") String region() default "US";

    @AttributeDefinition(name = "Content Path", description = "Content path associated with the report") String contentPath() default "/content/ecm/us/en";

    @AttributeDefinition(name = "Report Type", description = "Type of regional content report") String reportType() default "MISSING_DESCRIPTION";
}