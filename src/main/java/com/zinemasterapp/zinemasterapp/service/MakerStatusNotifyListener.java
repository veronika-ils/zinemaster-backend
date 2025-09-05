package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.model.RequestStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

// MakerStatusNotifyListener.java
@Component
@RequiredArgsConstructor
public class MakerStatusNotifyListener {

    private final SimpMessagingTemplate messaging;
    private final PresenceService presence;
    private final NotificationUpdateHelper notification;
    private final NotificationCounterService notificationCounterService;
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStatusChangedone(RequestStatusChangedEvent e) {
        String maker = e.getMakerUsername();
        String makerId = e.getMakerId();
        System.out.println("Listener something");

        var payload = Map.of(
                "type", "STATUS_CHANGED",
                "requestId", e.getRequestId(),
                "newStatus", e.getNewStatus(),
                "decidedBy", e.getDecidedBy(),
                "changedAt", e.getChangedAt().toString(),
                "summary", "Request " + e.getRequestId() + " → " + e.getNewStatus()
        );



        if (!presence.isOnline(maker)) {
            //notification.incrementProcessedRequestsByUserId(makerId);
            notificationCounterService.bumpOffline(makerId);
        }else{
            notificationCounterService.bumpUnseen(makerId);
        }

    System.out.println("ITS KINDA WORKING");

        messaging.convertAndSendToUser(maker, "/queue/status", payload);
        System.out.println("Listener");
        System.out.println("LISTENER IS WORKING");


    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStatusChanged(RequestStatusChangedEvent e) {
        try {

            notificationCounterService.bumpUnseen(e.getMakerUsername());


            var payload = java.util.Map.of(
                    "type","status_changed",
                    "requestId", e.getRequestId(),
                    "newStatus", e.getNewStatus(),
                    "status", e.getNewStatus(),
                    "to", e.getMakerUsername(),
                    "decidedBy", e.getDecidedBy(),
                    "changedAt", e.getChangedAt().toString()
            );

            if (!presence.isOnline(e.getMakerUsername())) {
                //notification.incrementProcessedRequestsByUserId(makerId);
                notificationCounterService.bumpOffline(e.getMakerId());
            }else{
                notificationCounterService.bumpUnseen(e.getMakerId());
            }
            messaging.convertAndSendToUser(e.getMakerUsername(), "/queue/status", payload);

            log.info("AFTER_COMMIT: counter bumped and WS sent to user={} req={}",
                    e.getMakerUsername(), e.getRequestId());

        } catch (Exception ex) {

            log.error("AFTER_COMMIT listener failed for user={}, req={}",
                    e.getMakerUsername(), e.getRequestId(), ex);
        }
    }


    private boolean shouldNotifyMaker(String status) {

        return !"PENDING".equalsIgnoreCase(status);
    }


    public static class ProcessedNotificationDto {
        public final String type;
        public final String summary;
        public final String requestId;
        public final String status;
        public final String decidedBy;
        public final Instant changedAt;
        public ProcessedNotificationDto(String type, String summary, String requestId, String status, String decidedBy, Instant changedAt) {
            this.summary = summary;
            this.requestId = requestId; this.status = status; this.decidedBy = decidedBy; this.changedAt = changedAt;
            this.type = type;
        }
    }
}



