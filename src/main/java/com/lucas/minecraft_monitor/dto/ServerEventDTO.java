package com.lucas.minecraft_monitor.dto;

import com.lucas.minecraft_monitor.model.ServerEvent;

import java.time.LocalDateTime;

public record ServerEventDTO(
        Long id,
        Long serverId,
        String type,
        LocalDateTime createdAt
) {

    public static ServerEventDTO fromEntity(ServerEvent event) {
        return new ServerEventDTO(
                event.getId(),
                event.getServer().getId(),
                event.getType().name(),
                event.getCreatedAt()
        );
    }
}