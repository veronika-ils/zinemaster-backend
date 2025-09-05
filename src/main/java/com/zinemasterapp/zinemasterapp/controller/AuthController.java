package com.zinemasterapp.zinemasterapp.controller;


import com.zinemasterapp.zinemasterapp.dto.LoginRequest;
import com.zinemasterapp.zinemasterapp.dto.UserDTO;
import com.zinemasterapp.zinemasterapp.model.PasswordResetToken;
import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.TokenRepository;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import com.zinemasterapp.zinemasterapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController//deka e REST API kontroler, vrakja JSON
@RequestMapping("/api/auth")//osnovna ruta
public class AuthController {

    private final UserRepository userRepository;//isto e kako @Autowired ama povekje imam so konstructor koristeno
    private final TokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;//service za isprakjanje emails
    @Autowired
    private PasswordEncoder passwordEncoder;//za enkodiranje na pass



    public AuthController(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/login")// koga se pravi metod POST na ovaa specificna ruta
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {//JSON teloto go zema i go mapira vo LoginRequest
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);//dali ima vo baza korisnik vakov(so pomos na interfejsot
        // )
        System.out.println("Login request received from " + request.getUsername());//proverka


        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }
        if(user.getAccess() == 0){
            return ResponseEntity.status(401).body("User is not active");
        }


        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {//dali e ist so od baza pass
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Invalid password");
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        Optional<User> userOpt = userRepository.findByUsername(username);//dali postoi vo baza
        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();//namerno e bez seckanje na UUID za da e pobezbedno
            PasswordResetToken prt = new PasswordResetToken();//kreirame token
            prt.setToken(token);//tokenot
            prt.setUser(userOpt.get());//korisnikot
            prt.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            tokenRepository.save(prt);
            String email = userOpt.get().getEmail();

            String resetLink = "http://localhost:8082/reset-password?token=" + token;
            emailService.sendResetLink(email, resetLink);//prakja email

            return ResponseEntity.ok("Линкот е успешно пратен.");
        }
        return ResponseEntity.ok("That username is not found.");

    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));//dali go imame vo baza tokenot -> linija 79/82
        if (prt.getExpiresAt().isBefore(LocalDateTime.now())) {//dali pominalo 15min
            return ResponseEntity.status(HttpStatus.GONE).body("Token expired");
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(prt);//da ne se koristi pak tokenot

        return ResponseEntity.ok("Password has been reset.");
    }

    @GetMapping( "/me")//(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE) drug nacin,isto e samo sto forcame da vrakja json
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal UserDetails userDetails) {//ova e od spring security i go rpoveruva tokenot
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println(userDetails.getUsername());

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getEmailVerified(),
                user.getAuthProvider(),
                user.getGoogleSub(),
                user.getProfilePic(),
                user.getAddress(),
                user.getStartDate(),
                user.getUserType(),
                user.getAccess()
        );
        System.out.println("I am sending my dto");
        return ResponseEntity.ok(dto);
    }

}
