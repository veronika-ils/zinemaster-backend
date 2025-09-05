package com.zinemasterapp.zinemasterapp.model;

import java.time.Instant;

public class RequestStatusChangedEvent {
    private final String requestId;
    private final String makerId;
    private final String makerUsername;
    private final String newStatus;
    private final String decidedBy;
    private final Instant changedAt;

    public RequestStatusChangedEvent(String requestId,
                                     String makerId,
                                     String makerUsername,
                                     String newStatus,
                                     String decidedBy,
                                     Instant changedAt) {
        this.requestId = requestId;
        this.makerId = makerId;
        this.makerUsername = makerUsername;
        this.newStatus = newStatus;
        this.decidedBy = decidedBy;
        this.changedAt = changedAt;
    }

    public String getRequestId() { return requestId; }
    public String getMakerId() { return makerId; }
    public String getMakerUsername() { return makerUsername; }
    public String getNewStatus() { return newStatus; }
    public String getDecidedBy() { return decidedBy; }
    public Instant getChangedAt() { return changedAt; }
}
