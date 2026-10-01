package com.aem.ecm.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;

@Component(
        service = WorkflowProcess.class,
        property = {
                "process.label=ECM - Initialize Asset Metadata"
        }
)
public class InitializeAssetMetadataProcess implements WorkflowProcess {

    private static final Logger LOG =
            LoggerFactory.getLogger(InitializeAssetMetadataProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

        Object payloadObject =
                workItem.getWorkflowData().getPayload();

        if (payloadObject == null) {
            throw new WorkflowException(
                    "Workflow payload is missing");
        }

        String payload = payloadObject.toString();

        if (payload.isBlank() || !payload.startsWith("/")) {
            throw new WorkflowException(
                    "Workflow payload is not a valid JCR path: "
                            + payload);
        }

        // Use the workflow-provided session.
        Session session = workflowSession.adaptTo(Session.class);

        try {
            if (!session.nodeExists(payload)) {
                throw new WorkflowException(
                        "Workflow payload does not exist: "
                                + payload);
            }

            Node assetNode = session.getNode(payload);

            if (!assetNode.isNodeType("dam:Asset")) {
                throw new WorkflowException(
                        "Workflow payload is not a dam:Asset: "
                                + payload);
            }

            if (!assetNode.hasNode("jcr:content")) {
                throw new WorkflowException(
                        "Asset has no jcr:content node: "
                                + payload);
            }

            Node contentNode =
                    assetNode.getNode("jcr:content");

            if (!contentNode.hasNode("metadata")) {
                throw new WorkflowException(
                        "Asset has no metadata node: "
                                + payload);
            }

            Node metadataNode =
                    contentNode.getNode("metadata");

            // Asset filename, e.g. banner.jpg
            String originalFileName =
                    assetNode.getName();

            boolean initialized =
                    metadataNode.hasProperty(
                            "ecmMetadataInitialized")
                            && metadataNode
                            .getProperty("ecmMetadataInitialized")
                            .getBoolean();

            if (!initialized) {

                metadataNode.setProperty(
                        "ecmAssetStatus",
                        "PENDING_REVIEW");

                metadataNode.setProperty(
                        "ecmMetadataInitialized",
                        true);

                metadataNode.setProperty(
                        "ecmMetadataInitializedAt",
                        Calendar.getInstance());

                metadataNode.setProperty(
                        "ecmOriginalFileName",
                        originalFileName);

                LOG.info(
                        "Initialized asset metadata: {}",
                        payload);

            } else {

                LOG.info(
                        "Metadata already initialized; " +
                                "asset status will not be reset: {}",
                        payload);
            }

            session.save();

        } catch (RepositoryException e) {

            LOG.error(
                    "Repository operation failed for asset: {}",
                    payload,
                    e);

            throw new WorkflowException(
                    "Failed to initialize metadata for asset: "
                            + payload,
                    e);
        }

    }
}
