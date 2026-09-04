package com.lucas.minecraft_monitor.dto;

public record AggregateStatisticsDTO(
        int totalServers,
        long onlineServers,
        long offlineServers,
        double uptimePercentage
) {
}
