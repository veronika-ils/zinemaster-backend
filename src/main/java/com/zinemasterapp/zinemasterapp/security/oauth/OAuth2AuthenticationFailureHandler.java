package com.zinemasterapp.zinemasterapp.security.oauth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.ServletException;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Value("${app.oauth2.post-login-redirect}")//od application.properties
    private String postLoginRedirect;//kaj ke go pratime

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,//koristime Servleti deka sme an low-level,del od Spring Security
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String targetUrl = postLoginRedirect + "?error=oauth_failed";

        response.sendRedirect(targetUrl);//ova e nie sto prakjame
    }
}
