package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.dto.Notificationdto;
import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestNotificationService {
    private final PresenceService presence;
    private final SimpMessagingTemplate messaging;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.ui.requests-url-base:http://localhost:8082/requests/}")
    private String requestsUrlBase;

    @Value("${app.mail.from:no-reply@your-domain.com}")
    private String fromAddress;

    public RequestNotificationService(PresenceService presence, SimpMessagingTemplate messaging, UserRepository userRepository, EmailService emailService) {
        this.presence = presence; this.messaging = messaging;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void notifyAdmins(Notificationdto payload) {
        for (String adminUsername : presence.getOnlineAdmins()) {
            messaging.convertAndSendToUser(adminUsername, "/queue/requests", payload);
        }

        var allAdmins = findAllAdmins();
        for (User admin : allAdmins) {
            String username = admin.getUsername();
            if (presence.getOnlineAdmins().contains(username)) continue;
            String to = safeEmail(admin.getEmail());
            if (to == null) continue;

            String link = (requestsUrlBase.endsWith("/") ? requestsUrlBase : requestsUrlBase + "/");
            String subject = "New Request " + payload.getRequestId();
            String body =
                    "Hello,\n\n" +
                            "A new request " + payload.getRequestId() + " (" + payload.getItemCount() + " item" +
                            (payload.getItemCount() == 1 ? "" : "s") + ") was created by " + payload.getCreatedBy() + ".\n" +
                            "Open: " + link + "\n\n" +
                            "You are receiving this because you were offline.\n";

            try {

                emailService.sendEmail(to, subject, body);
            } catch (Exception e) {

                System.err.println("Email send failed to " + to + ": " + e.getMessage());
            }
        }
    }
    private List<User> findAllAdmins() {

            return userRepository.findByUserTypeAndAccess("ProductAdministrator", 1);

    }
    private static boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }
    private static String safeEmail(String e) {
        return (e != null && !e.isBlank()) ? e : null;
    }
}

