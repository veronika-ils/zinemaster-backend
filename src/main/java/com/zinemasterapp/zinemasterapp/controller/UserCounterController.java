package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import com.zinemasterapp.zinemasterapp.service.NotificationCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor//ova e za da ne e potreben konstruktor
public class UserCounterController {//isto ova moze i vo usercontroller, vaka e po pregledno
    private final UserRepository users;
    private final NotificationCounterService counters;

    @GetMapping("/{username}/counters")
    public Map<String,Object> counters(@PathVariable String username) {
        var u = users.findByUsername(username).orElseThrow();
        return Map.of(
                "unread", Optional.of(u.getUnreadNotificationCount()).orElse(0),
                "pending", Optional.of(u.getPendingEmailCount()).orElse(0),
                "processed", Optional.of(u.getRequestsProcessed()).orElse(0)
        );
    }

    @PostMapping("/{username}/unread/reset")
    public void resetUnread(@PathVariable String username) {
        counters.resetUnread(username);
    }

    @GetMapping("/{username}/status/unseen-count")
    public int getUnseen(@PathVariable String username) {
        var u = users.findByUsername(username).orElseThrow();
        return counters.unseen(u.getId());
    }

    @PostMapping("/{username}/status/unseen/reset")
    public void resetUnseen(@PathVariable String username) {
        var u = users.findByUsername(username).orElseThrow();
        counters.resetUnseen(u.getId());
    }
}

