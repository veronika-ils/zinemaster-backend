package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceService {
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    private final NotificationCounterService notificationCounterService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messaging;

    public PresenceService(NotificationCounterService notificationCounterService, UserRepository userRepository, SimpMessagingTemplate messaging) {
        this.notificationCounterService = notificationCounterService;
        this.userRepository = userRepository;
        this.messaging = messaging;
    }

    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var a = (Authentication) e.getUser();
        if (a != null)
        {
            onlineUsers.add(a.getName());
            String username = a.getName();
            notificationCounterService.transferPendingToUnread(username);
            User u = userRepository.findByUsername(username).orElse(null);
            notificationCounterService.transferToUnseen(u.getId());
            System.out.println("DONE "+u.getUsername());
        }

    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        var a = (Authentication) e.getUser();
        if (a != null) onlineUsers.remove(a.getName());
    }

    public boolean isOnline(String username) { return onlineUsers.contains(username); }
    public Set<String> getOnlineUsers() { return onlineUsers; }
}

