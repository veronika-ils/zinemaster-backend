package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import com.zinemasterapp.zinemasterapp.service.NotificationCounterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Map;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationCounterService counters;
    private final UserRepository userRepo;

    public NotificationController(NotificationCounterService counters, UserRepository userRepo) {
        this.counters = counters; this.userRepo = userRepo;
    }

    private User requireUser(Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/unread-count")
    public Map<String,Integer> unreadCount(Principal principal) {
        var user = requireUser(principal);
        return Map.of("unread", counters.getUnread(user.getId()));
    }

    @PostMapping("/reset-unread")
    public ResponseEntity<Void> resetUnread(Principal principal) {
        var user = requireUser(principal);
        counters.reset(user.getId());
        return ResponseEntity.noContent().build();
    }
}



