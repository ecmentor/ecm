package com.aem.ecm.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "ECM - Notification Channel",
        description = "Creates a notification channel configuration"
)
public @interface NotificationChannelConfig {
        @AttributeDefinition(
                name = "Channel Name",
                description = "Display name of the Notification Channel"
        )
        String channelName() default "";

        @AttributeDefinition(
                name = "Channel Code",
                description = "Unique Channel code such as Email, SMS, Mobile"
        )
        String channelCode() default "";

        @AttributeDefinition(
                name = "Sender",
                description = "Support email address for the Sender"
        )
        String sender() default "test";

        @AttributeDefinition(
                name = "Enabled",
                description = "Enable the channel"
        )
        String enabled() default "";

         @AttributeDefinition(
            name = "Priority",
            description = "Priority of the channel"
         )
        String priority() default "";

}
