package com.zinemasterapp.zinemasterapp.security;

import com.zinemasterapp.zinemasterapp.security.oauth.OAuth2AuthenticationFailureHandler;
import com.zinemasterapp.zinemasterapp.security.oauth.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//za bezbednost e ova
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(OAuth2AuthenticationSuccessHandler successHandler,
                          OAuth2AuthenticationFailureHandler failureHandler, JwtAuthenticationFilter jwtAuthenticationFilter)
    {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {//ova go koristime vo usercontroller
        return new BCryptPasswordEncoder();//enkriptirani se passwords
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();//za login i proverka na podatoci
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//.IF_REQUIRED
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/oauth2/**","/login/oauth2/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/requests/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/products/*/reservations-by-month").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                        .requestMatchers(HttpMethod.POST, "/dev/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/products/**").permitAll()
                        .requestMatchers("/api/users/*/processed-count").permitAll()
                        .requestMatchers("/api/users/*/processed-count/reset").permitAll()
                        .requestMatchers("/api/users/*/status/unseen-count").permitAll()
                        .requestMatchers("/api/users/*/status/unseen/reset").permitAll()
                        .requestMatchers("/api/dev/ping-admins").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/requests/**").permitAll()// mora za put eksplicitno da kazam
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/products/**",
                                "/api/requests/**",
                                "/api/users/**",
                                "/api/categories/**",
                                "/api/uploads/**",
                                "/stomp/**",
                                "/uploads/**",//ova e za da mozat da se zemat slikite
                                "/api/dev/**",
                                "/ws/**",
                                "/api/admin/**",
                                "/dev/**",
                                "/api/auth/login"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                      // .requestMatchers("/api/**").authenticated()
                       // .anyRequest().permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(o -> o
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8082","http://192.168.0.14:8082")); //ovde e frontendot
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));//koj metodi gi dozvoluvame
        config.setAllowedHeaders(List.of("*"));//koj headeri gi dozvoluvame
        config.setAllowCredentials(true);//za cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//za koj URLS ova sea so go definiravme vazi
        source.registerCorsConfiguration("/**", config);//ova znaci deka za site vazi
        return source;
    }
}
