package com.zinemasterapp.zinemasterapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
//za bezbednost e ova
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//enkriptirani se passwords
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/requests/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/requests/**").permitAll()// mora za put eksplicitno da kazam
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/products/**",
                                "/api/requests/**",
                                "/api/users/**",
                                "/api/categories/**"
                        ).permitAll()//samo ovie moze bez loggin se drugo mora logged in
                        .anyRequest().authenticated()
                       // .anyRequest().permitAll()
                );

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8082")); //ovde e frontendot
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));//koj metodi gi dozvoluvame
        config.setAllowedHeaders(List.of("*"));//koj headeri gi dozvoluvame
        config.setAllowCredentials(false);//za cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//za koj URLS ova sea so go definiravme vazi
        source.registerCorsConfiguration("/**", config);//ova znaci deka za site vazi
        return source;
    }
}
