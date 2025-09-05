package com.zinemasterapp.zinemasterapp.security;

import com.zinemasterapp.zinemasterapp.security.jwt.JwtChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    public WebSocketConfig(JwtChannelInterceptor jwtChannelInterceptor) {
                this.jwtChannelInterceptor = jwtChannelInterceptor;
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")//patekata
                .setAllowedOriginPatterns("*")
                .withSockJS();//ako kaj klientot nemoze so websocket togas e ao ajax
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");//vo /topic site klienti dobivaat edno ensto sto se pratilo a so /queue samo eden klient dobiva(private e)
        config.setApplicationDestinationPrefixes("/app");//na pr /app/hello samo ke go prati na url so /hello
        config.setUserDestinationPrefix("/user");//za specificni korisnici
    }
    @Override
   public void configureClientInboundChannel(ChannelRegistration registration) {
               registration.interceptors(jwtChannelInterceptor);
           }

}

