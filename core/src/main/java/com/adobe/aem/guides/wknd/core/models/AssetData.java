package com.adobe.aem.guides.wknd.core.models;

import java.util.Calendar;

public class AssetData {
    private String name;
    private String path;
    private String lastModified;
    private String mimeType;
    private String modifiedBy;
    private String lockedBy;

    // Getters and Setters
    private String status;

    private String lastApprovedDate;

    private String lastENapprovedDate;
    private String enStatus;
    private String lastESapprovedDate;
    private String esStatus;

    private boolean readyStatus;

    private boolean enReady;
    private boolean esReady;
    private Calendar enReadyTimestamp;
    private Calendar esReadyTimestamp;

    // Getter and Setter for readyStatus
    public Boolean getReadyStatus() {
        return readyStatus;
    }

    public void setReadyStatus(boolean readyStatus) {
        this.readyStatus = readyStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
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

    public String getLastENapprovedDate() {
        return lastENapprovedDate;
    }

    public void setLastENapprovedDate(String lastENapprovedDate) {
        this.lastENapprovedDate = lastENapprovedDate;
    }

    public String getEnStatus() {
        return enStatus;
    }

    public void setEnStatus(String enStatus) {
        this.enStatus = enStatus;
    }

    public String getLastESapprovedDate() {
        return lastESapprovedDate;
    }

    public void setLastESapprovedDate(String lastESapprovedDate) {
        this.lastESapprovedDate = lastESapprovedDate;
    }

    public String getEsStatus() {
        return esStatus;
    }

    public void setEsStatus(String esStatus) {
        this.esStatus = esStatus;
    }


    public String getLastApprovedDate() {
        return lastApprovedDate;
    }

    public void setLastApprovedDate(String lastApprovedDate) {
        this.lastApprovedDate = lastApprovedDate;
    }

    public boolean isEnReady() {
        return enReady;
    }

    public void setEnReady(boolean enReady) {
        this.enReady = enReady;
    }

    public boolean isEsReady() {
        return esReady;
    }

    public void setEsReady(boolean esReady) {
        this.esReady = esReady;
    }

    public Calendar getEnReadyTimestamp() {
        return enReadyTimestamp;
    }

    public void setEnReadyTimestamp(Calendar enReadyTimestamp) {
        this.enReadyTimestamp = enReadyTimestamp;
    }

    public Calendar getEsReadyTimestamp() {
        return esReadyTimestamp;
    }

    public void setEsReadyTimestamp(Calendar esReadyTimestamp) {
        this.esReadyTimestamp = esReadyTimestamp;
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
                ", status='" + status + '\'' +
                ", lastApprovedDate='" + lastApprovedDate + '\'' +
                ", lastENapprovedDate='" + lastENapprovedDate + '\'' +
                ", enStatus='" + enStatus + '\'' +
                ", lastESapprovedDate='" + lastESapprovedDate + '\'' +
                ", esStatus='" + esStatus + '\'' +
                ", readyStatus=" + readyStatus +
                '}';
    }
}
