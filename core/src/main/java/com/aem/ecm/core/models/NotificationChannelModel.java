package com.aem.ecm.core.models;

import com.aem.ecm.core.services.NotificationChannelDirectory;
import com.aem.ecm.core.services.NotificationChannelService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Model(
        adaptables = SlingHttpServletRequest.class,
        resourceType = NotificationChannelModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NotificationChannelModel {

    public static final String RESOURCE_TYPE =
            "ecm/components/ecm-channel";

    @OSGiService
    private NotificationChannelDirectory notificationChannelDirectory;

    public List<NotificationChannelService> getChannels() {
        if (notificationChannelDirectory == null) {
            return Collections.emptyList();
        }

        return notificationChannelDirectory.getEnabledChannels()
                .stream()
                .sorted(
                        Comparator.comparing(
                                NotificationChannelService::getChannelName
                        )
                )
                .collect(Collectors.toList());
    }

}