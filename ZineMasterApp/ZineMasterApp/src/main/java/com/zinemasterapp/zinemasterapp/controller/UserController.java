package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8082")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/{id}/profile-pic")//sakame da smenime profilna i mora da e PUT bidejki POST ne e idempotentno
    public ResponseEntity<Void> updateProfilePic(@PathVariable String id, @RequestBody Map<String, String> body) {
        String profilePic = body.get("profilePic");
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePic(profilePic);
        userRepository.save(user);

        System.out.println("Updating profile picture for user " + id + " to " + profilePic);//proverka

        return ResponseEntity.ok().build();
    }
}
