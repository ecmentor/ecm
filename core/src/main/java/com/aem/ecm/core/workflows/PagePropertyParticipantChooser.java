package com.aem.ecm.core.workflows;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.PayloadMap;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
        service = ParticipantStepChooser.class,
        property = {
                ParticipantStepChooser.SERVICE_PROPERTY_LABEL
                        + "=ECM - Page Property Reviewer"
        }
)
public class PagePropertyParticipantChooser
        implements ParticipantStepChooser {

    private static final Logger LOG =
            LoggerFactory.getLogger(PagePropertyParticipantChooser.class);

    private static final String DEFAULT_REVIEWER_PROPERTY =
            "workflowReviewer";

    private static final String DEFAULT_FALLBACK_USER =
            "admin";

    @Override
    public String getParticipant(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap arguments) throws WorkflowException {

        String processArguments =
                arguments.get("PROCESS_ARGS", String.class);

        String reviewerProperty = getArgument(
                processArguments,
                "property",
                DEFAULT_REVIEWER_PROPERTY);

        String fallbackUser = getArgument(
                processArguments,
                "fallback",
                DEFAULT_FALLBACK_USER);

        /*
         * This example expects the workflow payload to be
         * a page or a page's jcr:content node.
         */
        if (!PayloadMap.TYPE_JCR_PATH.equals(
                workItem.getWorkflowData().getPayloadType())) {

            LOG.warn(
                    "Payload is not a JCR path. Assigning work item to {}",
                    fallbackUser);

            return fallbackUser;
        }

        Session session = workflowSession.adaptTo(Session.class);

        if (session == null) {
            throw new WorkflowException(
                    "Unable to obtain a JCR session from WorkflowSession");
        }

        String payloadPath =
                workItem.getWorkflowData().getPayload().toString();

        String contentPath = payloadPath.endsWith("/jcr:content")
                ? payloadPath
                : payloadPath + "/jcr:content";

        try {
            if (!session.nodeExists(contentPath)) {
                LOG.warn(
                        "Content node {} does not exist. Using fallback user {}",
                        contentPath,
                        fallbackUser);

                return fallbackUser;
            }

            Node contentNode = session.getNode(contentPath);

            if (!contentNode.hasProperty(reviewerProperty)) {
                LOG.warn(
                        "Property {} is missing on {}. Using fallback user {}",
                        reviewerProperty,
                        contentPath,
                        fallbackUser);

                return fallbackUser;
            }

            String reviewerId = contentNode
                    .getProperty(reviewerProperty)
                    .getString();

            if (reviewerId == null ||
                    reviewerId.trim().isEmpty()) {

                LOG.warn(
                        "Property {} is empty. Using fallback user {}",
                        reviewerProperty,
                        fallbackUser);

                return fallbackUser;
            }

            reviewerId = reviewerId.trim();

            LOG.info(
                    "Assigning workflow work item for {} to {}",
                    payloadPath,
                    reviewerId);

            return reviewerId;

        } catch (RepositoryException exception) {
            throw new WorkflowException(
                    "Unable to resolve the reviewer for " + payloadPath,
                    exception);
        }
    }

    private String getArgument(
            String processArguments,
            String argumentName,
            String defaultValue) {

        if (processArguments == null ||
                processArguments.trim().isEmpty()) {
            return defaultValue;
        }

        String[] argumentEntries = processArguments.split(",");

        for (String argumentEntry : argumentEntries) {
            String[] keyValue = argumentEntry.trim().split("=", 2);

            if (keyValue.length == 2 &&
                    argumentName.equals(keyValue[0].trim())) {

                String value = keyValue[1].trim();

                if (!value.isEmpty()) {
                    return value;
                }
            }
        }

        return defaultValue;
    }
}