package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.model.RequestCreatedEvent;
import com.zinemasterapp.zinemasterapp.model.RequestStatusChangedEvent;
import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.ProductRepository;
import com.zinemasterapp.zinemasterapp.repository.ProductRequestRepository;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final ApplicationEventPublisher events;
    private final ProductRequestRepository repo;
    private final UserRepository userRepo;

    @Transactional(propagation = Propagation.MANDATORY)
    public void notifyStatusChanged(String requestId,
                                    String makerId,
                                    String makerUsername,
                                    String newStatus,
                                    String decidedBy) {
        events.publishEvent(new RequestStatusChangedEvent(
                requestId, makerId, makerUsername, newStatus, decidedBy, Instant.now()
        ));
    }

    @Transactional
    public void updateStatusAndNotify(String requestId, String newStatus, String decidedBy) {
        var req = repo.findById(requestId).orElseThrow();
        req.setStatus(newStatus);



        User user = userRepo.findById(req.getUserId()).orElseThrow();
        events.publishEvent(new RequestStatusChangedEvent(
                req.getId(),
                req.getUserId(),
                user.getUsername(),
                newStatus,
                decidedBy,
                java.time.Instant.now()));
    }
}



