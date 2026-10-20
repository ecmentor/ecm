package com.aem.ecm.core.services;

public interface ReadingTimeService {
    int calculateReadingTime(String text);

    int getWordsPerMinute();

    int getMinimumReadingTime();

    String getReadingTimeLabel();
}