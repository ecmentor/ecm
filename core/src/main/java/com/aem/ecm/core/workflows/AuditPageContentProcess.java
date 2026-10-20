package com.aem.ecm.core.workflows;

import com.adobe.granite.workflow.PayloadMap;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.jackrabbit.JcrConstants;
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
                "process.label=ECM - Content Audit Step Process"
        }
)
public class AuditPageContentProcess implements WorkflowProcess {

    private static final Logger LOG =
            LoggerFactory.getLogger(AuditPageContentProcess.class);

    private static final String PROCESS_LABEL =
            "ECM - Content Audit Step Process";

    private static final String PROCESS_ARGS = "PROCESS_ARGS";
    private static final String JCR_CONTENT = "jcr:content";
    private static final String DEFAULT_STATUS =
            "PROCESS_STEP_EXECUTED";

    private static final String PN_AUDIT_STATUS =
            "ecmAuditStatus";

    private static final String PN_AUDITED_BY =
            "ecmAuditedBy";

    private static final String PN_AUDITED_AT =
            "ecmAuditedAt";

    private static final String PN_WORKFLOW_TITLE =
            "ecmWorkflowTitle";

    @Override
    public void execute(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap arguments) throws WorkflowException {

        Object payloadObject =
                workItem.getWorkflowData().getPayload();

        // Payload validation
        if (payloadObject == null) {
            throw new WorkflowException(
                    "Workflow payload is missing");
        }

        String payload = payloadObject.toString();

        if (payload.isBlank() || !payload.startsWith("/")) {
            LOG.error("Invalid workflow payload: {}", payload);

            throw new WorkflowException(
                    "Workflow payload is not a valid JCR path: "
                            + payload);
        }
        Session session = workflowSession.adaptTo(Session.class);
        try {
            // Verify that the payload exists
            if (!session.nodeExists(payload)) {
                LOG.error(
                        "Workflow payload does not exist: {}",
                        payload);

                throw new WorkflowException(
                        "Workflow payload does not exist: "
                                + payload);
            }

            Node pageNode = session.getNode(payload);

            // Verify jcr:content
            if (!pageNode.hasNode(JCR_CONTENT)) {
                LOG.error(
                        "Page has no jcr:content node: {}",
                        payload);

                throw new WorkflowException(
                        "Page has no jcr:content node: "
                                + payload);
            }

            Node contentNode =
                    pageNode.getNode(JCR_CONTENT);

            // Get workflow initiator
            String initiator =
                    workItem.getWorkflow().getInitiator();

            if (initiator == null || initiator.isBlank()) {
                LOG.error(
                        "Workflow initiator is missing: {}",
                        payload);

                throw new WorkflowException(
                        "Workflow initiator ID is missing or blank");
            }

            // Get workflow model title
            String workflowTitle =
                    workItem.getWorkflow()
                            .getWorkflowModel()
                            .getTitle();

            if (workflowTitle == null || workflowTitle.isBlank()) {
                LOG.error(
                        "Workflow model title is missing: {}",
                        payload);

                throw new WorkflowException(
                        "Workflow model title is missing");
            }


            contentNode.setProperty(
                    PN_AUDIT_STATUS,
                    "CHECKED"
            );

            contentNode.setProperty(
                    PN_AUDITED_BY,
                    initiator
            );

            contentNode.setProperty(
                    PN_AUDITED_AT,
                    Calendar.getInstance()
            );

            contentNode.setProperty(
                    PN_WORKFLOW_TITLE,
                    workItem.getWorkflow().getWorkflowModel().getTitle()
            );

            session.save();

            LOG.info(
                    "Workflow process updated page {}",
                    payload
            );

        } catch (RepositoryException exception) {
            throw new WorkflowException(
                    "Unable to update workflow properties for payload: "
                            + payload,
                    exception
            );
        }
    }

}
