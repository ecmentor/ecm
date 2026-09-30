package com.aem.ecm.core.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component(
        service = NotificationChannelDirectory.class,
        immediate = true
)
public class NotificationChannelDirectory {

    private final List<NotificationChannelService> channelServices =
            new CopyOnWriteArrayList<>();

    @Reference(service = NotificationChannelService.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC)

    protected void bindNotificationChannel(
            NotificationChannelService notificationChannel) {

        channelServices.add(notificationChannel);
    }

    protected void unbindNotificationChannel(
            NotificationChannelService notificationChannel) {

        channelServices.remove(notificationChannel);
    }
    public List<NotificationChannelService> getAllChannels() {
        return Collections.unmodifiableList(
                new ArrayList<>(channelServices)
        );
    }


    public List<NotificationChannelService> getEnabledChannels() {

        return channelServices.stream()
                .filter(NotificationChannelService::isEnabled)
                .sorted(Comparator.comparingInt(
                        NotificationChannelService::getPriority
                ))
                .collect(Collectors.toList());
    }

    public NotificationChannelService getChannelByChannelCode(
            String channelCode) {

        if (channelCode == null) {
            return null;
        }

        return channelServices.stream()
                .filter(channel ->
                        channelCode.equalsIgnoreCase(
                                channel.getChannelCode()
                        )
                )
                .findFirst()
                .orElse(null);
    }

}