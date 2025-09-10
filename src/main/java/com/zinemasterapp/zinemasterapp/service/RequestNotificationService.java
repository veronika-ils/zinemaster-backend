package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.dto.Notificationdto;
import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RequestNotificationService {
    private final PresenceService presence;
    private final SimpMessagingTemplate messaging;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationCounterService notificationCounterService;
    private final NotificationUpdateHelper notificationUpdateHelper;

    @Value("${app.ui.requests-url-base:http://localhost:8082/requests/}")
    private String requestsUrlBase;

    public RequestNotificationService(PresenceService presence, SimpMessagingTemplate messaging, UserRepository userRepository, EmailService emailService, NotificationCounterService notificationCounterService, NotificationUpdateHelper notificationUpdateHelper) {
        this.presence = presence; this.messaging = messaging;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.notificationCounterService = notificationCounterService;

        this.notificationUpdateHelper = notificationUpdateHelper;
    }

    public void notifyAdmins(Notificationdto payload) {
        String actorUsername = payload.getCreatedBy(); // username
        for (String adminUsername : presence.getOnlineUsers()) {
            //if (adminUsername.equalsIgnoreCase(actorUsername)) continue;
            try {
                notificationCounterService.increment(null, adminUsername);
            } catch (Exception e) {
                System.err.println("Failed to update notification counter");
            }


            messaging.convertAndSendToUser(adminUsername, "/queue/requests", payload);

        }


        var allAdmins = findAllAdmins();
        for (User admin : allAdmins) {
            String username = admin.getUsername();

            if (presence.isOnline(username)) continue;
           // notificationCounterService.increment(admin.getId(),admin.getUsername());//sekako mora brojkata da se zgolemi
           // notificationUpdateHelper.incrementUnreadById(admin.getId());


            notificationUpdateHelper.incrementPendingEmailById(admin.getId());
        }

    }
    private List<User> findAllAdmins() {
            return userRepository.findByUserTypeAndAccess("ProductAdministrator", 1);
    }

}

