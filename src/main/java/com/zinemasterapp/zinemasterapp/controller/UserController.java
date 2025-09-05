package com.zinemasterapp.zinemasterapp.controller;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;

import com.zinemasterapp.zinemasterapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/users")//http://localhost:8081/api/users
@CrossOrigin(origins = "http://localhost:8082")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;//za da go enkodirame passwordot

    private final UserRepository userRepository;
    @Autowired
    private EmailService emailService;

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
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @RequestBody Map<String, String> body) {//najlesno e so hash
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        String newRole = body.get("userType");
        User user = userOpt.get();
        user.setUserType(newRole);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/access")
    public ResponseEntity<?> updateAccess(@PathVariable String id, @RequestBody Map<String, Integer> body) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        user.setAccess(body.get("access"));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        System.out.println("Stigna za " + user.getName());//MORA DA IMA ADRESA KADE RABOTI KORISNIKOT!!

        String uniqueId;
        do {
            uniqueId = "U" + System.currentTimeMillis() + String.format("%03d", new Random().nextInt(1000));
        } while (userRepository.existsById(uniqueId));
        user.setId(uniqueId);

        String username = generateUniqueUsername(user.getName(), user.getSurname());
        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        String subject = "Ваши податоци за пристап до системот ZineMaster";
        String body = "Почитуван/а " + user.getName() + ",\n\n"
                + "Вашиот кориснички профил е успешно креиран.\n"
                + "Корисничко име: " + username + "\n"
                + "Лозинка: test123\n\n"
                + "Ве молиме најавете се и променете ја лозинката при прво користење.\n\n"
                + "Поздрав,\nZineMaster тим";

        emailService.sendEmail(user.getEmail(), subject, body);

        return ResponseEntity.ok().build();
    }


    private String generateUniqueUsername(String name, String surname) {
        String baseUsername = name.toLowerCase() + "." + surname.toLowerCase();
        String username = baseUsername;
        int counter = 1;

        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) return ResponseEntity.notFound().build();

        User user = optUser.get();
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setStartDate(updatedUser.getStartDate());
        user.setUserType(updatedUser.getUserType());
        user.setEmail(updatedUser.getEmail());
        user.setAddress(updatedUser.getAddress());//sve setirame

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}/processed-count")
    public ResponseEntity<Integer> getProcessedCount(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user.getRequestsProcessed()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/processed-count/reset")
    public ResponseEntity<Object> resetProcessedCount(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRequestsProcessed(0);
                    userRepository.save(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }





}
