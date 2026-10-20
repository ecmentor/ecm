package com.aem.ecm.core.services;

public interface NotificationChannelService
{
    String getChannelName();
    String getChannelCode();
    String getSender();
    boolean isEnabled();
    int getPriority();

}
