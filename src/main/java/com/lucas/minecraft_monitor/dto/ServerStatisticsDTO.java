package com.lucas.minecraft_monitor.dto;

public record ServerStatisticsDTO(
        int periodHours,
        long totalChecks,
        long onlineChecks,
        long offlineChecks,
        double uptimePercentage,
        double averagePlayers,
        int peakPlayers,
        double averageLatency
) {
}