package com.zinemasterapp.zinemasterapp.service;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceService {
    private final Set<String> onlineAdmins = ConcurrentHashMap.newKeySet();

    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var user = (Authentication) e.getUser();
        if (user != null && user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ProductAdministrator"))) {
            onlineAdmins.add(user.getName());
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        var user = (Authentication) e.getUser();
        if (user != null) onlineAdmins.remove(user.getName());
    }

    public Set<String> getOnlineAdmins() { return Set.copyOf(onlineAdmins); }
}

