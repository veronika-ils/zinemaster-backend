package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationUpdateHelper {

    private static final Logger log = LoggerFactory.getLogger(NotificationUpdateHelper.class);

    private final UserRepository userRepository;
    private final NotificationCounterService notificationCounterService;

    public NotificationUpdateHelper(UserRepository userRepository,
                                      NotificationCounterService notificationCounterService) {
        this.userRepository = userRepository;
        this.notificationCounterService = notificationCounterService;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementUnreadByUsername(String username) {

        notificationCounterService.increment(null, username);

        // userRepository.incrementUnreadByUsername(username);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementUnreadById(String userId) {
        notificationCounterService.increment(userId, null);

    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int incrementUnreadByIdOrUsername(String userId, String username) {
        if (userId != null && !userId.isBlank()) {
            return userRepository.incrementUnread(userId);
        } else if (username != null && !username.isBlank()) {
            return userRepository.incrementUnreadByUsername(username);
        }
        return 0;
    }

    // Pending email digest counter
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementPendingEmailById(String userId) {
        userRepository.incrementPendingEmailCount(userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementProcessedRequestsByUserId(String userId) {
        userRepository.incrementRequestsProcessed(userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetProcessedCount(String userId) {
        userRepository.resetProccessedRequests(userId);
    }

    @Transactional(readOnly = true)
    public int getProcessedCount(String userId) {
        Integer n = userRepository.getRequestsProcessed(userId);
        return n == null ? 0 : n;
    }

    @Transactional
    public void resetPending(String username) {
        userRepository.resetPendingEmail(username);
    }
}
