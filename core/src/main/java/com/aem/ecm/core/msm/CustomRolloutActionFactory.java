package com.aem.ecm.core.msm;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveActionFactory;

import com.day.cq.wcm.msm.commons.BaseActionFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

@Component(
        service = LiveActionFactory.class,
        property = {
                LiveActionFactory.LIVE_ACTION_NAME
                        + "=custom-sku-title-rollout"
        }
)
public class CustomRolloutActionFactory
        extends BaseActionFactory<CustomTitleRolloutAction> {

    @Override
    public String createsAction() {
        return "custom-sku-title-rollout";
    }
    @Override
    protected CustomTitleRolloutAction newActionInstance(ValueMap config) throws WCMException {
        return new CustomTitleRolloutAction(config, this);
    }
}