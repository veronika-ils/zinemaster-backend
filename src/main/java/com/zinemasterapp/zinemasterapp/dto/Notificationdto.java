package com.zinemasterapp.zinemasterapp.dto;

import java.time.Instant;

public class Notificationdto {
    String requestId;
    String createdBy;
    Instant createdAt;
    int itemCount;
    String summary;
    String type;


    public Notificationdto(String requestId, String createdBy, Instant createdAt, int itemCount, String summary,String type) {
        this.requestId = requestId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.itemCount = itemCount;
        this.summary = summary;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
