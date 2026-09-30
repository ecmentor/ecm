package com.aem.ecm.core.schedulers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.aem.ecm.core.config.RegionalReportSchedulerConfiguration;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = Job.class,
        immediate = true
)
@Designate(
        ocd = RegionalReportSchedulerConfiguration.class
)
public class RegionalReportSchedulerJob implements Job {

    private static final Logger LOG =
            LoggerFactory.getLogger(
                    RegionalReportSchedulerJob.class
            );

    private static final String CONFIG_REGION =
            "region";

    private static final String CONFIG_CONTENT_PATH =
            "contentPath";

    private static final String CONFIG_REPORT_TYPE =
            "reportType";

    @Reference
    private Scheduler scheduler;

    private String scheduledJobName;

    @Activate
    protected void activate(
            RegionalReportSchedulerConfiguration configuration) {

        LOG.info("Activating Regional Report Scheduler");

        schedule(configuration);
    }

    @Modified
    protected void modified(
            RegionalReportSchedulerConfiguration configuration) {

        LOG.info(
                "Regional Report Scheduler configuration changed"
        );

        schedule(configuration);
    }

    @Deactivate
    protected void deactivate() {

        LOG.info("Deactivating Regional Report Scheduler");

        unschedule();
    }

    private synchronized void schedule(
            RegionalReportSchedulerConfiguration configuration) {

        /*
         * Remove the previous schedule before registering
         * a new one.
         */
        unschedule();

        if (!configuration.enabled()) {
            LOG.info("Regional Report Scheduler is disabled");
            return;
        }

        String jobName =
                configuration.schedulerName().trim();

        String cronExpression =
                configuration.cronExpression().trim();

        if (jobName.isEmpty()) {
            LOG.error("Scheduler name cannot be empty");
            return;
        }

        if (cronExpression.isEmpty()) {
            LOG.error("Cron expression cannot be empty");
            return;
        }

        /*
         * These values will be passed to execute()
         * through JobContext.
         */
        Map<String, Serializable> jobConfiguration =
                new HashMap<>();

        jobConfiguration.put(
                CONFIG_REGION,
                configuration.region()
        );

        jobConfiguration.put(
                CONFIG_CONTENT_PATH,
                configuration.contentPath()
        );

        jobConfiguration.put(
                CONFIG_REPORT_TYPE,
                configuration.reportType()
        );

        try {
            ScheduleOptions options =
                    scheduler.EXPR(cronExpression);

            options.name(jobName);

            options.canRunConcurrently(false);

            options.config(jobConfiguration);

            boolean scheduled = scheduler.schedule(
                    this,
                    options
            );

            if (scheduled) {
                scheduledJobName = jobName;

                LOG.info(
                        "Scheduled job '{}' using expression '{}'",
                        jobName,
                        cronExpression
                );
            } else {
                LOG.error(
                        "Unable to schedule job '{}'",
                        jobName
                );
            }

        } catch (RuntimeException exception) {
            LOG.error(
                    "Unable to schedule job '{}' using expression '{}'",
                    jobName,
                    cronExpression,
                    exception
            );
        }
    }

    private synchronized void unschedule() {

        if (scheduledJobName == null) {
            return;
        }

        boolean removed = scheduler.unschedule(
                scheduledJobName
        );

        if (removed) {
            LOG.info(
                    "Unscheduled job '{}'",
                    scheduledJobName
            );
        } else {
            LOG.warn(
                    "Scheduled job '{}' was not found",
                    scheduledJobName
            );
        }

        scheduledJobName = null;
    }

    @Override
    public void execute(JobContext context) {

        /*
         * The Job interface gives us access to JobContext.
         */
        String jobName = context.getName();

        Map<String, Serializable> configuration =
                context.getConfiguration();

        String region = (String) configuration.get(
                CONFIG_REGION
        );

        String contentPath = (String) configuration.get(
                CONFIG_CONTENT_PATH
        );

        String reportType = (String) configuration.get(
                CONFIG_REPORT_TYPE
        );

        LOG.info(
                "Executing regional report job"
        );

        LOG.info(
                "Job name: {}",
                jobName
        );

        LOG.info(
                "Region: {}",
                region
        );

        LOG.info(
                "Content path: {}",
                contentPath
        );

        LOG.info(
                "Report type: {}",
                reportType
        );

        LOG.info(
                "Regional report job completed"
        );
    }
}