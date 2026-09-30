package com.aem.ecm.core.schedulers;

import java.time.Instant;

import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = Runnable.class,
        immediate = true,
        property = {
                Scheduler.PROPERTY_SCHEDULER_NAME
                        + "=ECM Scheduler Heartbeat",

                Scheduler.PROPERTY_SCHEDULER_EXPRESSION
                        + "=0 * * * * ?",

                Scheduler.PROPERTY_SCHEDULER_CONCURRENT
                        + ":Boolean=false"
        }
)
public class EcmSchedulerRunnableNow implements Runnable {

    private static final Logger LOG =
            LoggerFactory.getLogger(EcmSchedulerRunnableNow.class);

    @Override
    public void run() {
        LOG.info(
                "ECM Scheduler Heartbeat executed at {}",
                Instant.now()
        );
    }
}