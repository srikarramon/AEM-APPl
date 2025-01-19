package com.adobe.aem.guides.wknd.core.models;

import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component(service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/saveAssetStatus",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=GET"
        })
public class SaveAssetStatusServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SaveAssetStatusServlet.class);

    private static final String STATUS_PROPERTY = "status";
    private static final String STATUS_UPDATED_PROPERTY = "statusUpdated";
    private static final String EN_STATUS_PROPERTY = "en-status";
    private static final String LAST_EN_APPROVED_DATE_PROPERTY = "lastENapprovedDate";
    private static final String ES_STATUS_PROPERTY = "es-status";
    private static final String LAST_ES_APPROVED_DATE_PROPERTY = "lastESapprovedDate";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        String path = request.getParameter("path");
        String status = request.getParameter("status");

        LOG.info("Request received to update asset status. Path: {}, Status: {}", path, status);

        if (path == null || status == null) {
            respondWithError(response, SlingHttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: 'path' and 'status'.");
            return;
        }

        try {
            Resource assetResource = resourceResolver.getResource(path);

            if (assetResource != null) {
                Resource metadataResource = assetResource.getChild("jcr:content/metadata");
                if (metadataResource != null) {
                    ModifiableValueMap metadataProperties = metadataResource.adaptTo(ModifiableValueMap.class);

                    if (metadataProperties != null) {
                        if ("en-certified".equalsIgnoreCase(status)) {
                            updateMetadata(metadataProperties, EN_STATUS_PROPERTY, LAST_EN_APPROVED_DATE_PROPERTY);
                        } else if ("es-certified".equalsIgnoreCase(status)) {
                            updateMetadata(metadataProperties, ES_STATUS_PROPERTY, LAST_ES_APPROVED_DATE_PROPERTY);
                        } else {
                            respondWithError(response, SlingHttpServletResponse.SC_BAD_REQUEST, "Invalid status value: " + status);
                            return;
                        }

                        resourceResolver.commit();
                        respondWithSuccess(response, "Asset status updated successfully for path: " + path);
                    } else {
                        respondWithError(response, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to adapt metadata resource to ModifiableValueMap.");
                    }
                } else {
                    respondWithError(response, SlingHttpServletResponse.SC_NOT_FOUND, "Metadata node not found for asset: " + path);
                }
            } else {
                respondWithError(response, SlingHttpServletResponse.SC_NOT_FOUND, "Asset not found at path: " + path);
            }
        } catch (Exception e) {
            LOG.error("Error updating asset metadata for path: {}. Error: {}", path, e.getMessage(), e);
            respondWithError(response, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    private void updateMetadata(ModifiableValueMap metadataProperties, String statusProperty, String dateProperty) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        metadataProperties.put(statusProperty, "certified");
        metadataProperties.put(dateProperty, currentDate);
        LOG.info("Updated '{}' with 'certified' and '{}' with current date.", statusProperty, dateProperty);
    }

    private void respondWithSuccess(SlingHttpServletResponse response, String message) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("message", message);
        response.setContentType("application/json");
        response.setStatus(SlingHttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
    }

    private void respondWithError(SlingHttpServletResponse response, int statusCode, String message) throws IOException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("error", message);
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(jsonResponse.toString());
    }
}