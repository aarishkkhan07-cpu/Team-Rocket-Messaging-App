package com.example.team_rocket.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;


    public JwtService(
            @Value("${app.jwt.secret}")
            String secret
    ) {

        this.secretKey =
                Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(secret)
                );
    }


    public String generateToken(
            String username
    ) {

        Date issuedAt =
                new Date();


        Date expiration =
                new Date(
                        issuedAt.getTime()
                                + expirationMs
                );


        return Jwts.builder()

                .subject(username)

                .issuedAt(issuedAt)

                .expiration(expiration)

                .signWith(secretKey)

                .compact();
    }


    public String extractUsername(
            String token
    ) {

        return getClaims(token)
                .getSubject();
    }


    public boolean isTokenValid(
            String token,
            String username
    ) {

        try {

            Claims claims =
                    getClaims(token);


            String tokenUsername =
                    claims.getSubject();


            Date expiration =
                    claims.getExpiration();


            return tokenUsername.equals(username)
                    && expiration.after(
                            new Date()
                    );

        } catch (Exception exception) {

            return false;
        }
    }


    private Claims getClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(secretKey)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}