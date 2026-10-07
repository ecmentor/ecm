package com.aem.ecm.core.msm;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.commons.BaseAction;
import com.day.cq.wcm.msm.commons.BaseActionFactory;

/**
 * Custom MSM Live Action that appends a "(Synced)" suffix to the jcr:title of
 * a Live Copy page whenever it is rolled out from its Blueprint.
 *
 * Instances are created by {@link AppendSuffixActionFactory}.
 */
public class AppendSuffixLiveAction extends BaseAction {

    static final String SUFFIX = " (Synced)";

    protected AppendSuffixLiveAction(ValueMap config, BaseActionFactory<? extends LiveAction> factory) {
        super(config, factory);
    }

    @Override
    public String getName() {
        return AppendSuffixActionFactory.ACTION_NAME;
    }

    @Override
    protected boolean handles(Resource source, Resource target, LiveRelationship relation, boolean resetRollout) {
        // Only act when the target (Live Copy) resource carries a jcr:title to modify
        return target != null && target.getValueMap().get(NameConstants.PN_TITLE, String.class) != null;
    }

    @Override
    protected void doExecute(Resource source, Resource target, LiveRelationship relation, boolean resetRollout)
            throws RepositoryException, WCMException {
        ModifiableValueMap targetProperties = target.adaptTo(ModifiableValueMap.class);
        if (targetProperties == null) {
            return;
        }

        String title = targetProperties.get(NameConstants.PN_TITLE, String.class);
        if (title != null && !title.endsWith(SUFFIX)) {
            targetProperties.put(NameConstants.PN_TITLE, title + SUFFIX);
        }
    }
}
