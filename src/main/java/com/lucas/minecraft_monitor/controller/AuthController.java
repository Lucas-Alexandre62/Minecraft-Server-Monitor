package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.AuthRequest;
import com.lucas.minecraft_monitor.dto.AuthResponse;
import com.lucas.minecraft_monitor.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.expiration:3600}")
    private long expiration;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        String token =
                jwtService.generateToken(
                        authentication.getName(),
                        authentication.getAuthorities().stream()
                                .map(authority -> authority.getAuthority())
                                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                                .toList()
                );

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        "Bearer",
                        expiration
                )
        );
    }
}