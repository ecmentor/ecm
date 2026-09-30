package com.aem.ecm.core.schedulers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.aem.ecm.core.config.RegionalReportSchedulerConfiguration;
import com.aem.ecm.core.utility.ResourceResolverUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Job.class, immediate = true)
@Designate(ocd = RegionalReportSchedulerConfiguration.class)
public class ReadPageSchedulerJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(ReadPageSchedulerJob.class);

    private static final String SUBSERVICE_NAME = "regional-content-reader";

    private static final String CONFIG_REGION = "region";
    private static final String CONFIG_CONTENT_PATH = "contentPath";
    private static final String CONFIG_REPORT_TYPE = "reportType";

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverUtil resourceResolverUtil;

    private String scheduledJobName;

    @Activate
    protected void activate(RegionalReportSchedulerConfiguration configuration) {
        schedule(configuration);
    }

    @Modified
    protected void modified(RegionalReportSchedulerConfiguration configuration) {
        schedule(configuration);
    }

    @Deactivate
    protected void deactivate() {
        unschedule();
    }

    private synchronized void schedule(RegionalReportSchedulerConfiguration configuration) {
        unschedule();

        if (!configuration.enabled()) {
            LOG.info("Regional report scheduler is disabled: {}", configuration.schedulerName());
            return;
        }

        Map<String, Serializable> jobConfiguration = new HashMap<>();

        jobConfiguration.put(CONFIG_REGION, configuration.region());

        jobConfiguration.put(CONFIG_CONTENT_PATH, configuration.contentPath());

        jobConfiguration.put(CONFIG_REPORT_TYPE, configuration.reportType());

        ScheduleOptions options = scheduler.EXPR(configuration.cronExpression());

        options.name(configuration.schedulerName());
        options.canRunConcurrently(false);
        options.config(jobConfiguration);

        boolean scheduled = scheduler.schedule(this, options);

        if (scheduled) {
            scheduledJobName = configuration.schedulerName();

            LOG.info("Scheduled regional report: name={}, cron={}", configuration.schedulerName(), configuration.cronExpression());
        } else {
            LOG.error("Unable to schedule regional report: {}", configuration.schedulerName());
        }
    }

    private synchronized void unschedule() {
        if (scheduledJobName != null) {
            scheduler.unschedule(scheduledJobName);

            LOG.info("Unscheduled regional report: {}", scheduledJobName);

            scheduledJobName = null;
        }
    }

    @Override
    public void execute(JobContext context) {

        Map<String, Serializable> configuration = context.getConfiguration();

        String region = getConfigurationValue(configuration, CONFIG_REGION);

        String contentPath = getConfigurationValue(configuration, CONFIG_CONTENT_PATH);

        String reportType = getConfigurationValue(configuration, CONFIG_REPORT_TYPE);

        LOG.info("Executing regional report: jobName={}, " + "region={}, contentPath={}, reportType={}", context.getName(), region, contentPath, reportType);

        readPage(context.getName(), region, contentPath, reportType);
    }

    private void readPage(String jobName, String region, String contentPath, String reportType) {
        try (ResourceResolver resourceResolver = resourceResolverUtil.getServiceResourceResolver(SUBSERVICE_NAME)) {

            Resource pageResource = resourceResolver.getResource(contentPath);

            if (pageResource == null) {
                LOG.warn("Resource does not exist or is not accessible: {}", contentPath);
                return;
            }

            Resource contentResource = pageResource.getChild(JcrConstants.JCR_CONTENT);

            if (contentResource == null) {
                LOG.warn("jcr:content was not found under: {}", contentPath);
                return;
            }

            ValueMap properties = contentResource.getValueMap();

            String pageTitle = properties.get(JcrConstants.JCR_TITLE, "(title not available)");

            LOG.info("Regional content read succeeded: " + "jobName={}, region={}, " + "contentPath={}, reportType={}, " + "pageTitle={}, serviceUser={}", jobName, region, contentPath, reportType, pageTitle, resourceResolver.getUserID());

        } catch (LoginException exception) {
            LOG.error("Unable to obtain a service ResourceResolver " + "for subservice '{}'. " + "The service-user mapping has not " + "been configured.", SUBSERVICE_NAME, exception);
        }
    }

    private String getConfigurationValue(Map<String, Serializable> configuration, String propertyName) {
        if (configuration == null) {
            return "";
        }

        Serializable value = configuration.get(propertyName);

        return value != null ? value.toString() : "";
    }
}