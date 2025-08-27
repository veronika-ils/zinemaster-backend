package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.dto.Notificationdto;
import com.zinemasterapp.zinemasterapp.dto.UserDTO;
import com.zinemasterapp.zinemasterapp.service.RequestNotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

//samo za DEMO(proba dali rabotat notifikaciite ako e nekoj online) -> vo krajniot sistem nema da ima vakvo kopce
@RestController
@RequestMapping("/api/dev")//pocetok na url
public class DevNotifyController {

    private final RequestNotificationService notifier;//servis za prakjanje notifikacii

    public DevNotifyController(RequestNotificationService notifier) {
        this.notifier = notifier;
    }

    @PostMapping("/ping-admins")
    @PreAuthorize("hasRole('ProductAdministrator')")//samo ako e administrator na produkti
    public ResponseEntity<Void> pingAdmins(@AuthenticationPrincipal org.springframework.security.core.userdetails.User me) {//samo vrakja http status
        var payload = new Notificationdto(
                "TEST-" + UUID.randomUUID().toString().substring(0,8).toUpperCase(),
                me.getUsername(),
                Instant.now(),
                1,
                "Test notification from " + me.getUsername()
        );
        notifier.notifyAdmins(payload);
        return ResponseEntity.noContent().build();
    }
}

