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
    public Message<?> preSend(Message<?> message, MessageChannel channel) {//stom dojde poraka od klientot
        StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);//da go citame zaglavjeto
        if (acc != null && StompCommand.CONNECT.equals(acc.getCommand())) {//ako e connect(online e)
            String auth = acc.getFirstNativeHeader("Authorization"); //tokenot
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);//trgame bearer
                String username = jwtService.extractUsername(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authn = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                acc.setUser(authn);//da moze da znaeme koja sesija za koj korisnik e
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

