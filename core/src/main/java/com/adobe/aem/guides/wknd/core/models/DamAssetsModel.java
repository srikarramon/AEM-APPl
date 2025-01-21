package com.adobe.aem.guides.wknd.core.models;

import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DamAssetsModel {

    private static final Logger log = LoggerFactory.getLogger(DamAssetsModel.class);

    @Self
    private Resource resource;

    @ValueMapValue
    @Default(values = {"/content/dam/wknd/en"}) // Default value for DAM paths
    private String[] damPaths; // Reading as String array

    private List<AssetData> assetList;

    @PostConstruct
    protected void init() {
        log.info("Initializing DamAssetsModel");

        assetList = new ArrayList<>();

        if (damPaths == null || damPaths.length == 0) {
            log.error("No DAM paths configured for this component.");
            return;
        }
        log.debug("dampaths  {} ", damPaths);
        ResourceResolver resourceResolver = resource.getResourceResolver();
        if (resourceResolver == null) {
            log.error("ResourceResolver is null, cannot resolve DAM paths.");
            return;
        }

        for (String damPath : damPaths) {
            if (damPath == null || damPath.trim().isEmpty()) {
                log.warn("Skipping invalid or empty DAM path.");
                continue;
            }

            Resource damResource = resourceResolver.getResource(damPath.trim());
            if (damResource == null) {
                log.warn("No resource found at path: {}", damPath);
                continue;
            }

            log.info("Processing DAM path: {}", damPath);

            for (Resource child : damResource.getChildren()) {
                Asset asset = child.adaptTo(Asset.class);
                if (asset != null) {
                    AssetData assetData = new AssetData();
                    assetData.setName(asset.getName());
                    assetData.setPath(asset.getPath());
                    populateAssetMetadata(resourceResolver, asset, assetData);
                    assetList.add(assetData);
                } else {
                    log.debug("Child at path {} is not an asset.", child.getPath());
                }
            }
        }

        log.info("Asset list generation completed. Total assets: {}", assetList.size());
    }

    private void populateAssetMetadata(ResourceResolver resourceResolver, Asset asset, AssetData assetData) {
        try {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                log.warn("Session is null, skipping metadata retrieval.");
                return;
            }

            String metadataPath = asset.getPath() + "/jcr:content/metadata";
            String nodePath = asset.getPath() + "/jcr:content";
            Node metadataNode = session.nodeExists(metadataPath) ? session.getNode(metadataPath) : null;
            Node contentNode =  session.nodeExists(nodePath) ? session.getNode(nodePath) : null;

            if (metadataNode != null) {
                // Set the lastModified value, checking metadataNode first, then contentNode, and defaulting to "N/A"
                    // Set the lastModified value, checking contentNode first, then metadataNode, and defaulting to "N/A"
                    assetData.setLastModified(
                            contentNode != null && contentNode.hasProperty("jcr:lastModified")
                                    ? contentNode.getProperty("jcr:lastModified").getString()
                                    : (metadataNode != null && metadataNode.hasProperty("jcr:lastModified")
                                    ? metadataNode.getProperty("jcr:lastModified").getString()
                                    : "N/A")
                    );
                assetData.setModifiedBy(
                        metadataNode != null && metadataNode.hasProperty("jcr:lastModifiedBy")
                                ? metadataNode.getProperty("jcr:lastModifiedBy").getString()
                                : (contentNode != null && contentNode.hasProperty("jcr:lastModifiedBy")
                                ? contentNode.getProperty("jcr:lastModifiedBy").getString()
                                : "N/A")
                );
                assetData.setLockedBy(
                        metadataNode != null && metadataNode.hasProperty("cq:lockOwner")
                                ? metadataNode.getProperty("cq:lockOwner").getString()
                                : (contentNode != null && contentNode.hasProperty("cq:lockOwner")
                                ? contentNode.getProperty("cq:lockOwner").getString()
                                : "N/A")
                );
                assetData.setStatus(
                        metadataNode != null && metadataNode.hasProperty("status")
                                ? metadataNode.getProperty("status").getString()
                                : "unknown" // Default value
                );
                assetData.setLastApprovedDate(
                        metadataNode != null && metadataNode.hasProperty("lastApprovedDate")
                                ? metadataNode.getProperty("lastApprovedDate").getString()
                                : "N/A"
                );
                assetData.setLastENapprovedDate(
                        metadataNode.hasProperty("lastENapprovedDate")
                                ? metadataNode.getProperty("lastENapprovedDate").getString()
                                : "N/A"
                );
                assetData.setEnStatus(
                        metadataNode.hasProperty("en-status")
                                ? metadataNode.getProperty("en-status").getString()
                                : "unknown"
                );
                assetData.setLastESapprovedDate(
                        metadataNode.hasProperty("lastESapprovedDate")
                                ? metadataNode.getProperty("lastESapprovedDate").getString()
                                : "N/A"
                );
                assetData.setEsStatus(
                        metadataNode.hasProperty("es-status")
                                ? metadataNode.getProperty("es-status").getString()
                                : "unknown"
                );
                assetData.setEnReady(
                        metadataNode.hasProperty("enReady")
                                ? metadataNode.getProperty("enReady").getBoolean()
                                : false
                );

                // Set esReady property
                assetData.setEsReady(
                        metadataNode.hasProperty("esReady")
                                ? metadataNode.getProperty("esReady").getBoolean()
                                : false
                );

                // Set enReadyTimestamp property
                assetData.setEnReadyTimestamp(
                        metadataNode.hasProperty("enReadyTimestamp")
                                ? metadataNode.getProperty("enReadyTimestamp").getDate()
                                : null
                );

                // Set esReadyTimestamp property
                assetData.setEsReadyTimestamp(
                        metadataNode.hasProperty("esReadyTimestamp")
                                ? metadataNode.getProperty("esReadyTimestamp").getDate()
                                : null
                );
                if (metadataNode != null && metadataNode.hasProperty("readyStatus")) {
                    assetData.setReadyStatus(metadataNode.getProperty("readyStatus").getBoolean());
                    log.info("Asset readyStatus set to: {}", metadataNode.getProperty("readyStatus").getBoolean());
                } else {
                    assetData.setReadyStatus(false); // Default or fallback value
                    log.info("Asset readyStatus set to default: false");
                }
                log.debug("ReadyStatus for asset {}: {}", asset.getPath(), assetData.getReadyStatus());
            } else {
                log.debug("Metadata node not found for asset: {}", asset.getPath());
            }
        } catch (RepositoryException e) {
            log.error("Error retrieving metadata for asset at path: {}", asset.getPath(), e);
        }
    }

    public List<AssetData> getAssetList() {
        return Collections.unmodifiableList(assetList);
    }
}