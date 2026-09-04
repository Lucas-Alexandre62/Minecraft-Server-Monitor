package com.lucas.minecraft_monitor.dto;

import com.lucas.minecraft_monitor.model.ServerStatusHistory;

import java.time.LocalDateTime;

public record ServerStatusHistoryDTO(
        Long id,
        boolean online,
        int playersOnline,
        int maxPlayers,
        long latency,
        String version,
        LocalDateTime checkedAt
) {

    public static ServerStatusHistoryDTO fromEntity(
            ServerStatusHistory history
    ) {
        return new ServerStatusHistoryDTO(
                history.getId(),
                history.isOnline(),
                history.getPlayersOnline(),
                history.getMaxPlayers(),
                history.getLatency(),
                history.getVersion(),
                history.getCheckedAt()
        );
    }
}