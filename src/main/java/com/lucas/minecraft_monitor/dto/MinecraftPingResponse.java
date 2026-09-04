package com.lucas.minecraft_monitor.dto;

public record MinecraftPingResponse(
        int playersOnline,
        int maxPlayers,
        String version,
        String motd
) {
}