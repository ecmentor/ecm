package com.aem.ecm.core.workflows;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.PayloadMap;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
        service = WorkflowProcess.class,
        property = {
                "process.label=ECM - Set Page Workflow Status"
        }
)
public class SetPageWorkflowStatusProcess implements WorkflowProcess {

    private static final Logger LOG =
            LoggerFactory.getLogger(SetPageWorkflowStatusProcess.class);

    private static final String PROCESS_LABEL =
            "ECM - Set Page Workflow Status";

    private static final String PROCESS_ARGS = "PROCESS_ARGS";

    private static final String DEFAULT_STATUS =
            "PROCESS_STEP_EXECUTED";

    private static final String PN_WORKFLOW_STATUS =
            "ecmWorkflowStatus";

    private static final String PN_WORKFLOW_PROCESS =
            "ecmWorkflowProcess";

    private static final String PN_PROCESSED_AT =
            "ecmWorkflowProcessedAt";

    @Override
    public void execute(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap arguments) throws WorkflowException {

        /*
         * This example supports page paths such as:
         * /content/ecm/us/en
         */
        if (!PayloadMap.TYPE_JCR_PATH.equals(
                workItem.getWorkflowData().getPayloadType())) {

            throw new WorkflowException(
                    "Unsupported payload type: "
                            + workItem.getWorkflowData().getPayloadType()
            );
        }

        String payloadPath =
                workItem.getWorkflowData().getPayload().toString();

        String status = getArgument(
                arguments,
                "status",
                DEFAULT_STATUS
        );

        Session session = workflowSession.adaptTo(Session.class);

        if (session == null) {
            throw new WorkflowException(
                    "Could not obtain a JCR Session from WorkflowSession"
            );
        }

        String contentPath = getContentPath(payloadPath);

        try {
            if (!session.nodeExists(contentPath)) {
                throw new WorkflowException(
                        "Page content node does not exist: " + contentPath
                );
            }

            Node contentNode = session.getNode(contentPath);

            contentNode.setProperty(
                    PN_WORKFLOW_STATUS,
                    status
            );

            contentNode.setProperty(
                    PN_WORKFLOW_PROCESS,
                    PROCESS_LABEL
            );

            contentNode.setProperty(
                    PN_PROCESSED_AT,
                    Calendar.getInstance()
            );

            session.save();

            LOG.info(
                    "Workflow process updated page {} with status {}",
                    payloadPath,
                    status
            );

        } catch (RepositoryException exception) {
            throw new WorkflowException(
                    "Unable to update workflow properties for payload: "
                            + payloadPath,
                    exception
            );
        }
    }

    /**
     * Converts a page payload into its content-node path.
     *
     * /content/ecm/us/en
     * becomes
     * /content/ecm/us/en/jcr:content
     */
    private String getContentPath(String payloadPath) {

        String jcrContentSuffix =
                "/" + JcrConstants.JCR_CONTENT;

        if (payloadPath.endsWith(jcrContentSuffix)) {
            return payloadPath;
        }

        return payloadPath + jcrContentSuffix;
    }

    /**
     * Reads comma-separated Process Step arguments.
     *
     * Example:
     * status=PROCESS_STEP_EXECUTED
     */
    private String getArgument(
            MetaDataMap arguments,
            String argumentName,
            String defaultValue) {

        String processArguments =
                arguments.get(PROCESS_ARGS, "");

        if (processArguments == null
                || processArguments.trim().isEmpty()) {
            return defaultValue;
        }

        for (String argument : processArguments.split(",")) {

            String[] nameValue =
                    argument.trim().split("=", 2);

            if (nameValue.length == 2
                    && argumentName.equals(nameValue[0].trim())) {

                String value = nameValue[1].trim();

                if (!value.isEmpty()) {
                    return value;
                }
            }
        }

        return defaultValue;
    }
}