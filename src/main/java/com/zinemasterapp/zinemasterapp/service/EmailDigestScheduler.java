package com.zinemasterapp.zinemasterapp.service;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EmailDigestScheduler {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationUpdateHelper notificationUpdateHelper;
    private  final NotificationCounterService notificationCounterService;

    public EmailDigestScheduler(UserRepository userRepository, EmailService emailService, NotificationUpdateHelper notificationUpdateHelper, NotificationCounterService notificationCounterService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.notificationUpdateHelper = notificationUpdateHelper;
        this.notificationCounterService = notificationCounterService;
    }

    @Scheduled(cron = "0 0 8-18 * * * ")
    @Transactional
    public void sendDigestEmails() {
        var admins = userRepository.findAdminsWithPending();

        for (User admin : admins) {
            int count = admin.getPendingEmailCount();
            if (count == 0) continue;

            String subject = "[ZineMaster] "+ count +" New Requests";
            String body = "Hello"+admin.getUsername()+",\n\nYou have " + count + " new request"
                    + (count == 1 ? "" : "s") + " since the last digest. You are receiving this e-mail because you are offline.\n\n"
                    + "-ZineMaster";

            emailService.sendEmail(admin.getEmail(), subject, body);

            admin.setPendingEmailCount(0);
            notificationCounterService.resetPending(admin.getId());
            userRepository.save(admin);
        }
    }


    @Scheduled(cron = "0 0 8-18 * * * ")
    public void sendProcessedDigests() {
        for (var user : userRepository.findAll()) {
            int n = notificationUpdateHelper.getProcessedCount(user.getId());
            if (n <= 0) continue;

            String subject = "[ZineMaster] " + n + " of your requests were processed";
            String body = "Hello " + user.getUsername() + ",\n\n"
                    + n + " of your requests were processed (approved/rejected).\n"
                    + "Check your My Requests page for details.\n\n"
                    + "— ZineMaster";

            emailService.sendEmail(user.getEmail(), subject, body);
            userRepository.resetProccessedRequests(user.getId());
        }
    }

}

