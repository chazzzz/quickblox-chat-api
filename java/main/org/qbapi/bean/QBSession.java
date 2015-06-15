package org.qbapi.bean;

import java.util.Date;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBSession {

    private String rawInfo;

    private Long applicationId;

    private String id;

    private String token;

    private Long timestamp;

    private Date createdAt;

    private Date updatedAt;

    public void setRawInfo(String rawInfo) {
        this.rawInfo = rawInfo;
    }

    public String getRawInfo() {
        return rawInfo;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
