package com.aem.ecm.core.services.impl;

import com.aem.ecm.core.config.NotificationChannelConfig;
import com.aem.ecm.core.services.NotificationChannelService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = NotificationChannelService.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
        ocd = NotificationChannelConfig.class,
        factory = true
)

public class NotificationChannelServiceImpl implements NotificationChannelService {

    private static final Logger LOG =
                LoggerFactory.getLogger(NotificationChannelServiceImpl.class);

        private volatile String channelName;
        private volatile String channelCode;
        private volatile String sender;
        private volatile boolean enabled;
        private volatile int priority;

        @Activate
        @Modified
        protected void activate(NotificationChannelConfig config) {
        channelName = config.channelName();
        channelCode = config.channelCode();
        sender = config.sender();
        enabled = Boolean.parseBoolean(config.enabled());
        priority = Integer.parseInt(config.priority());

        LOG.info(
                "Notification Channel activated: channelName={}, channelCode={}, senderEmail={}",
                channelName,
                channelCode,
                sender
        );
    }

        @Override
        public String getChannelName() {
        return channelName;
    }

        @Override
        public String getChannelCode() {
        return channelCode;
    }

        @Override
        public String getSender() {
        return sender;
    }

        @Override
        public boolean isEnabled() {
        return enabled;
    }
         @Override
         public int getPriority() {
            return priority;
    }
}
