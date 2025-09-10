package com.zinemasterapp.zinemasterapp.security.oauth;

import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import com.zinemasterapp.zinemasterapp.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import jakarta.servlet.ServletException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;


@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${app.oauth2.post-login-redirect}")
    private String frontendRedirect;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User p = (OAuth2User) authentication.getPrincipal();//ova e toa sto Google ni ima vrateno

        String email    = (String) p.getAttribute("email");
        String name     = (String) p.getAttribute("name");
        String picture  = (String) p.getAttribute("picture");
        Boolean verified = (Boolean) p.getAttribute("email_verified");


        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {

            String target = UriComponentsBuilder.fromUriString(frontendRedirect)
                    .queryParam("error", "no_account_for_email")//ako ne e vo db
                    .build(true).toUriString();
            response.sendRedirect(target);
            return;
        }

        User user = userOpt.get();

        if (user.getAccess() == 0) {
            String target = UriComponentsBuilder.fromUriString(frontendRedirect)
                    .queryParam("error", "account_inactive")//ako ne e aktiven
                    .build(true).toUriString();
            response.sendRedirect(target);
            return;
        }

        if (picture != null) user.setProfilePic(picture);
        if (verified != null) user.setEmailVerified(verified);
        user.setAuthProvider("google");
        userRepository.save(user);


        String jwt = jwtService.createToken(
                user.getId(),
                user.getUsername(),
                user.getUserType(),
                user.getEmail()
        );

        System.out.println("[OAUTH] frontendRedirect=" + frontendRedirect);

        String target = UriComponentsBuilder.fromUriString(frontendRedirect)
                .queryParam("token", jwt)//stavame kveri parametar token=...
                .build(true).toUriString();
        System.out.println("[OAUTH] redirecting to " + target);

        response.sendRedirect(target);
    }

}
