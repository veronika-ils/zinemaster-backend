package com.zinemasterapp.zinemasterapp.security.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {//za da moze da gi vidime websocket porakite(online/offline)
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtChannelInterceptor(JwtService jwtService, UserDetailsService uds) {
        this.jwtService = jwtService; this.userDetailsService = uds;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);//headerite gi zema, CONNECT,SUBSCRIBE
        if (acc != null && StompCommand.CONNECT.equals(acc.getCommand())) {//samo na prviot CONNECT
            String auth = acc.getFirstNativeHeader("Authorization");//dali e avtoriziran
            if (auth != null && auth.startsWith("Bearer ")) {//ako e
                String token = auth.substring(7);//tokenot
                Claims claims = jwtService.getClaims(token);//go citame tokenot
                String username = claims.getSubject();
                UserDetails user = userDetailsService.loadUserByUsername(username);
                Authentication a = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());//kreirame avtentikaciski korisnik
                acc.setUser(a);
            }
        }
        return message;
    }
}

@Configuration
class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtChannelInterceptor interceptor;//ova e toa pogore kodot^
    WebSocketAuthConfig(JwtChannelInterceptor i) { this.interceptor = i; }

    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {//pred kontrollerite mora da se proveri dali e konektirano
        reg.interceptors(interceptor);
    }
}

