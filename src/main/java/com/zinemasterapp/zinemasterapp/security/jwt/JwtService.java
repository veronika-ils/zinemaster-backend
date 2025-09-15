package com.zinemasterapp.zinemasterapp.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")//od applications.properties cita
    private String secret;

    @Value("${app.jwt.expiry-minutes:120}")
    private long expiryMinutes;


    public String createToken(String userId, String username, String role, String email) {
        Instant now = Instant.now();//ne e dovolno detailed za so LocalDate
        return Jwts.builder()
                .setSubject(username)//unique treba da e
                .claim("uid", userId)//claim se kroisti za stavanje vo teloto na tokenot,username:{value}
                .claim("role", role)
                .claim("email", email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofMinutes(expiryMinutes))))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)//kluc
                .compact();//go kreira krajniot token
    }

    public String extractUsername(String token) {

        return getClaims(token).get("username", String.class);//toj username jas go kreirav
    }


    public Authentication parse(String token) {
        Claims claims = getClaims(token);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        var authorities = List.of(new SimpleGrantedAuthority(role));
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }


    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }



    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = getClaims(token);
            String username = claims.getSubject();
            return username.equals(userDetails.getUsername()) && !isExpired(claims);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
