package com.aem.ecm.core.msm;

import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.commons.BaseActionFactory;

/**
 * Registers the {@link AppendSuffixLiveAction} with MSM so it can be referenced
 * by name ({@value #ACTION_NAME}) from a Rollout Configuration's action list
 * (see /apps/msm/wcm/rolloutconfigs/append-suffix in ui.apps).
 *
 * The {@code liveActionName} service property is required: MSM's
 * RolloutConfigManagerFactoryImpl indexes LiveActionFactory services purely by
 * this OSGi service reference property (it never invokes {@link #createsAction()}
 * at runtime), so without it this factory cannot be resolved and any
 * RolloutConfig referencing {@value #ACTION_NAME} is silently excluded.
 */
@Component(service = LiveActionFactory.class, property = {
        LiveActionFactory.LIVE_ACTION_NAME + "=" + AppendSuffixActionFactory.ACTION_NAME
})
public class AppendSuffixActionFactory extends BaseActionFactory<AppendSuffixLiveAction> {

    /**
     * Name under which this action is registered. Must match the name of the
     * {@code cq:LiveSyncAction} node configured in the Rollout Configuration page.
     */
    public static final String ACTION_NAME = "appendSuffix";

    @Override
    public String createsAction() {
        return ACTION_NAME;
    }

    @Override
    protected AppendSuffixLiveAction newActionInstance(ValueMap config) throws WCMException {
        return new AppendSuffixLiveAction(config, this);
    }
}
