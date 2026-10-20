package com.aem.ecm.core.services.impl;
import com.aem.ecm.core.config.ReadingTimeServiceConfig;
import com.aem.ecm.core.services.ReadingTimeService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = ReadingTimeService.class, immediate = true)
@Designate(ocd = ReadingTimeServiceConfig.class)

public class ReadingTimeServiceImpl implements ReadingTimeService {

    private int wordsPerMinute;
    private int minimumReadingTime;
    private String readingTimeLabel;

    @Activate
    @Modified
    protected void activate(ReadingTimeServiceConfig config) {
        wordsPerMinute = config.wordsPerMinute();
        minimumReadingTime = config.minimumReadingTime();
        readingTimeLabel = config.readingTimeLabel();
    }

    @Override
    public int calculateReadingTime(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        // Remove HTML tags
        String plainText = text
                .replaceAll("<[^>]*>", " ")
                .trim();

        // Text contained only HTML tags
        if (plainText.isEmpty()) {
            return 0;
        }
        int wordCount = text.trim().split("\\s+").length;

        return Math.max(
                1,
                (int) Math.ceil((double) wordCount / wordsPerMinute)
        );
    }

    @Override
    public int getWordsPerMinute() {
        return wordsPerMinute;
    }
    @Override
    public String getReadingTimeLabel() {
        return readingTimeLabel;
    }
    @Override
    public int getMinimumReadingTime() {
        return minimumReadingTime;
    }

}