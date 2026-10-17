package com.aem.ecm.core.workflows;

import javax.jcr.Node;
import javax.jcr.Session;

import org.osgi.service.component.annotations.Component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
        service = WorkflowProcess.class,
        property = {
                "process.label=ECM - Update Asset Review Metadata"
        }
)
public class UpdateAssetReviewMetadata implements WorkflowProcess {
    @Override
    public void execute(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap args) throws WorkflowException {

        String payload = workItem.getWorkflowData()
                .getPayload().toString();

        if (!payload.startsWith("/content/dam/ecm/training/")) {
            return;
        }

        try {
            Session session = workflowSession.adaptTo(Session.class);

            String metadataPath =
                    payload + "/jcr:content/metadata";

            if (!session.nodeExists(metadataPath)) {
                throw new WorkflowException(
                        "Metadata node not found: " + metadataPath);
            }

            Node metadata = session.getNode(metadataPath);

            metadata.setProperty(
                    "ecm:reviewStatus", "Pending Review");

            metadata.setProperty(
                    "ecm:processedBy", "Custom Workflow");

            session.save();

        } catch (Exception e) {
            throw new WorkflowException(
                    "Unable to update asset metadata", e);
        }
    }
}
