package com.aem.ecm.core.msm;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.commons.BaseAction;
import com.day.cq.wcm.msm.commons.BaseActionFactory;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

public class CustomTitleRolloutAction extends BaseAction {

    private static final Logger LOG =
            LoggerFactory.getLogger(CustomTitleRolloutAction.class);

    public CustomTitleRolloutAction(ValueMap config, BaseActionFactory<? extends LiveAction> factory) {
        super(config, factory);
    }

    @Override
    public String getName() {
        return "custom-sku-title-rollout";
    }

    @Override
    protected boolean handles(Resource source, Resource target, LiveRelationship relation, boolean isReset) throws RepositoryException, WCMException {
        return target != null && target.getValueMap().get(NameConstants.PN_TITLE, String.class) != null;
    }

    @Override
    protected void doExecute(Resource source, Resource target, LiveRelationship relation, boolean isReset) throws RepositoryException, WCMException {
        {
            if (source == null || target == null) {
                LOG.warn("Source or target resource is null");
                return;
            }

            ModifiableValueMap targetProperties =
                    target.adaptTo(ModifiableValueMap.class);

            if (targetProperties == null) {
                LOG.warn(
                        "Unable to adapt target to ModifiableValueMap: {}",
                        target.getPath()
                );
                return;
            }

            String sku = source.getValueMap().get("sku", String.class);

            if (sku == null || sku.trim().isEmpty()) {
                LOG.warn(
                        "SKU not found on source resource: {}",
                        source.getPath()
                );
                return;
            }

            String title = sku + "-SKU";

            targetProperties.put("jcr:title", title);

            LOG.info(
                    "Custom MSM rollout: {} -> {}",
                    target.getPath(),
                    title
            );
        }
    }
}