package com.lucas.minecraft_monitor.dto;

public record MinecraftServerStatusDTO(
        boolean online,
        String host,
        int port,
        int playersOnline,
        int maxPlayers,
        String version,
        long latency,
        String motd
) {
}