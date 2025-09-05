package com.zinemasterapp.zinemasterapp.security.jwt;

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
        StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (acc != null && StompCommand.CONNECT.equals(acc.getCommand())) {
            String h = acc.getFirstNativeHeader("Authorization");
            if (h != null && h.startsWith("Bearer ")) {
                String token = h.substring(7);
                Authentication auth = jwtService.parse(token);
                acc.setUser(auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
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

