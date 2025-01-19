package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Calendar;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=" + "/bin/markAssetReady",
                "sling.servlet.extensions=html"
        }
)
public class MarkAssetReadyServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(MarkAssetReadyServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String path = request.getParameter("path");
        String statusKey = request.getParameter("status");
        LOG.info("Status Key {} and Status value : {}", path, statusKey);

        if (path == null || statusKey == null) {
            LOG.error("Path, ready status, or status key is missing in the request.");
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing required parameters.");
            return;
        }

        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(path + "/jcr:content/metadata");

        if (resource == null) {
            LOG.error("Metadata node not found for path: {}", path);
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Metadata node not found.");
            return;
        }

        try {
            Node metadataNode = resource.adaptTo(Node.class);
            if (metadataNode != null) {
                // Update the status property dynamically
               // metadataNode.setProperty(statusKey, Boolean.parseBoolean(readyStatus));

                // Update the corresponding timestamp
                if ("esReady".equals(statusKey)) {
                    metadataNode.setProperty("esReady", true);
                    metadataNode.setProperty("esReadyTimestamp", Calendar.getInstance());
                    LOG.info("Updated esReady to {} and esReadyTimestamp for path: {}",statusKey , path);
                } else if ("enReady".equals(statusKey)) {
                    metadataNode.setProperty("enReady", true);
                    metadataNode.setProperty("enReadyTimestamp", Calendar.getInstance());
                    LOG.info("Updated enReady to {} and enReadyTimestamp for path: {}", statusKey, path);
                }

                // Save the session
                resourceResolver.adaptTo(javax.jcr.Session.class).save();
                response.setStatus(SlingHttpServletResponse.SC_OK);
                response.getWriter().write("Asset " + statusKey + " marked as ready successfully.");
            } else {
                LOG.error("Failed to adapt metadata resource to JCR Node for path: {}", path);
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to update metadata properties.");
            }
        } catch (RepositoryException e) {
            LOG.error("Error updating {} status for metadata node at path: {}", statusKey, path, e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error updating " + statusKey + " status.");
        }
    }
}