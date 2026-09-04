package com.lucas.minecraft_monitor.dto;

public record AuthResponse(
        String token,
        String type,
        long expiresIn
) {
}