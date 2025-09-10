package com.zinemasterapp.zinemasterapp.security;


import com.zinemasterapp.zinemasterapp.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;//service sto go vrakja username
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)//bez ova principle nemase da raboti
            throws IOException, ServletException {

        final String uri = req.getRequestURI();
        final String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("[JWT] No Bearer " + uri);
            chain.doFilter(req, res);//go prakjame vo SecurityConfig i ako ne mora da e avtenticiran i moze da dozvoli(toa so .permitAll())
            return;
        }

        final String token = header.substring(7);
        try {

            Claims claims = jwtService.getClaims(token);
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                username = claims.get("username", String.class);
            }
            System.out.println("[JWT] username" + username);

            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(req, res);
                return;
            }

            UserDetails user = userDetailsService.loadUserByUsername(username);
            boolean valid = jwtService.isTokenValid(token, user);
            if (valid) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
         System.out.println("[JWT] Invalid Bearer token");
        }

        chain.doFilter(req, res);
    }
}
