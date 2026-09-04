package com.lucas.minecraft_monitor.dto;

import com.lucas.minecraft_monitor.model.MinecraftServer;

public record MinecraftServerResponse(
        Long id,
        String name,
        String host,
        Integer port
) {
    public static MinecraftServerResponse fromEntity(MinecraftServer server) {
        return new MinecraftServerResponse(
                server.getId(),
                server.getName(),
                server.getHost(),
                server.getPort()
        );
    }
}
