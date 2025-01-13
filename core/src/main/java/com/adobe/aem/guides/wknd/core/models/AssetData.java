package com.adobe.aem.guides.wknd.core.models;

public class AssetData {
    private String name;
    private String path;
    private String lastModified;
    private String mimeType;
    private String modifiedBy;
    private String lockedBy;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    @Override
    public String toString() {
        return "AssetData{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", lockedBy='" + lockedBy + '\'' +
                '}';
    }
}
