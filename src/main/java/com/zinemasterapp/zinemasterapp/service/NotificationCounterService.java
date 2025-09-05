package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.repository.UserRepository;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class NotificationCounterService {

    private static final Logger log = LoggerFactory.getLogger(NotificationCounterService.class);

    private final UserRepository repo;


    public NotificationCounterService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increment(String userId, String username) {
        int rows = 0;

        if (userId != null && !userId.isBlank()) {
            rows = repo.incrementUnread(userId);
            log.debug("incrementUnread id={} -> rows={}", userId, rows);
        }

        // Fallback by username in case id doesn't match any row
        if (rows == 0 && username != null && !username.isBlank()) {
            rows = repo.incrementUnreadByUsername(username);
            log.debug("incrementUnreadByUsername username={} -> rows={}", username, rows);
        }

        if (rows == 0) {
            log.warn("Unread NOT incremented (no matching row) id={}, username={}", userId, username);
        }
    }

    @Transactional(readOnly = true)
    public int getUnread(String userId) {
        Integer v = repo.getUnread(userId);
        return v == null ? 0 : v;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reset(String userId) {
        int rows = repo.resetUnread(userId);
        log.debug("resetUnread id={} -> rows={}", userId, rows);
    }

    @Transactional
    public void incUnread(String username) { repo.incrementUnreadByUsername(username);}

    @Transactional
    public void incPending(String userId) { repo.incrementPendingEmailCount(userId); }

    @Transactional
    public void transferPendingToUnread(String username) {
        repo.transferPendingToUnread(username);
    }

    @Transactional
    public void resetUnread(String username) {
        repo.resetUnread(username);
    }

    @Transactional
    public void resetPending(String userId) {
        repo.resetPendingEmail(userId);
    }

    @Transactional public void bumpOffline(String ownerId) {
        repo.incrementRequestsProcessed(ownerId);
    }

    @Transactional public void bumpUnseen(String ownerId) {
        repo.incUnseenProcessedStatus(ownerId);
    }

    @Transactional public void transferToUnseen(String ownerId) {
        repo.transferProcessedToUnseen(ownerId);
    }

    @Transactional(readOnly = true)
    public int unseen(String ownerId) { return repo.getUnseenProcessedStatus(ownerId); }

    @Transactional public void resetUnseen(String ownerId) {
        repo.resetUnseenProcessedStatus(ownerId);
    }


}


