package com.zinemasterapp.zinemasterapp.model;

import java.time.Instant;

public final class RequestCreatedEvent {
    private final String requestId;
    private final String actorUsername;
    private final Instant at;

    public RequestCreatedEvent(String requestId, String actorUsername, Instant at) {
        this.requestId = requestId;
        this.actorUsername = actorUsername;
        this.at = at;
    }

    public String getRequestId() { return requestId; }
    public String getActorUsername() { return actorUsername; }
    public Instant getAt() { return at; }
}

