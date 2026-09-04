package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.dto.ServerStatisticsDTO;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServerStatisticsService {

    private final ServerStatusHistoryService historyService;

    public ServerStatisticsService(
            ServerStatusHistoryService historyService
    ) {
        this.historyService = historyService;
    }

    public ServerStatisticsDTO calculate(
            Long serverId,
            int hours
    ) {

        LocalDateTime since =
                LocalDateTime.now().minusHours(hours);

        List<ServerStatusHistory> history =
                historyService.findEntitiesByServerId(serverId, since);

        long totalChecks = history.size();

        long onlineChecks =
                history.stream()
                        .filter(ServerStatusHistory::isOnline)
                        .count();

        long offlineChecks =
                totalChecks - onlineChecks;

        double uptimePercentage =
                totalChecks == 0
                        ? 0.0
                        : (onlineChecks * 100.0) / totalChecks;

        double averagePlayers =
                history.isEmpty()
                        ? 0.0
                        : history.stream()
                        .mapToInt(
                                ServerStatusHistory::getPlayersOnline
                        )
                        .average()
                        .orElse(0.0);

        int peakPlayers =
                history.isEmpty()
                        ? 0
                        : history.stream()
                        .mapToInt(
                                ServerStatusHistory::getPlayersOnline
                        )
                        .max()
                        .orElse(0);

        double averageLatency =
                history.stream()
                        .filter(ServerStatusHistory::isOnline)
                        .mapToLong(
                                ServerStatusHistory::getLatency
                        )
                        .average()
                        .orElse(0.0);

        return new ServerStatisticsDTO(
                hours,
                totalChecks,
                onlineChecks,
                offlineChecks,
                uptimePercentage,
                averagePlayers,
                peakPlayers,
                averageLatency
        );
    }
}