package com.aem.ecm.core.models;

import com.aem.ecm.core.services.ReadingTimeService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = ArticleModel.class,
        resourceType = ArticleModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ArticleModel {

    public static final String RESOURCE_TYPE =
            "ecm/components/ecm-article";
    @ValueMapValue
    private String title;
    @ValueMapValue
    private String text;


    @OSGiService
    private ReadingTimeService readingTimeService;

    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
    public int getEstimatedReadingTime()
    {
        return readingTimeService.calculateReadingTime(text);
    }
    public String getReadingTimeLabel() {
        return readingTimeService.getReadingTimeLabel();
    }
}
