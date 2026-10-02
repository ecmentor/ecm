package com.aem.ecm.core.schedulers;

import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@Component(
            service = Runnable.class, immediate = true,
            property = {
                    Scheduler.PROPERTY_SCHEDULER_NAME + "=System Metrics Logger Scheduler",
                    Scheduler.PROPERTY_SCHEDULER_EXPRESSION + "=0 * * * * ?",
                    Scheduler.PROPERTY_SCHEDULER_CONCURRENT + ":Boolean=false"
            }
    )
    public class SystemMetricsLoggerScheduler implements Runnable {

        private static final Logger LOG =
                LoggerFactory.getLogger(SystemMetricsLoggerScheduler.class);

        @Override
        public void run() {
            LOG.info("System metrics tracking at {}", Instant.now());
        }
    }

