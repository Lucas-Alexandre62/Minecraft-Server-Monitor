package com.lucas.minecraft_monitor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration:3600}")
    private long expiration;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(String username) {

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expiration);

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(username)
                        .issuedAt(now)
                        .expiresAt(expiresAt)
                        .build();

        JwsHeader header =
                JwsHeader.with(MacAlgorithm.HS256)
                        .build();

        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(
                        header,
                        claims
                );

        return jwtEncoder
                .encode(parameters)
                .getTokenValue();
    }
}