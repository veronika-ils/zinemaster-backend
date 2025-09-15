package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.repository.UserRepository;

import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class NotificationCounterService {


    private final UserRepository repo;


    public NotificationCounterService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increment(String userId, String username) {
        int rows = 0;

        if (userId != null && !userId.isBlank()) {
            rows = repo.incrementUnread(userId);
            System.out.println("incrementUnread id=" + userId + " -> rows=" + rows);
        }


        if (rows == 0 && username != null && !username.isBlank()) {
            rows = repo.incrementUnreadByUsername(username);
            System.out.println("incrementUnreadByUsername username=" + username + " -> rows=" + rows);
        }

        if (rows == 0) {
            System.out.println("Unread NOT incremented username=" + username);
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
        System.out.println("resetUnread id=" + userId + " -> rows=" + rows);
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


