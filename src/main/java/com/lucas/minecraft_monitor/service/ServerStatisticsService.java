package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.dto.AggregateStatisticsDTO;
import com.lucas.minecraft_monitor.dto.ServerStatisticsDTO;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import com.lucas.minecraft_monitor.repository.ServerStatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServerStatisticsService {

    private final ServerStatusHistoryService historyService;
    private final ServerStatusHistoryRepository historyRepository;
    private final MinecraftServerRepository serverRepository;

    public ServerStatisticsService(
            ServerStatusHistoryService historyService,
            ServerStatusHistoryRepository historyRepository,
            MinecraftServerRepository serverRepository
    ) {
        this.historyService = historyService;
        this.historyRepository = historyRepository;
        this.serverRepository = serverRepository;
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

    public AggregateStatisticsDTO calculateAggregate(
            int hours
    ) {
        LocalDateTime since =
                LocalDateTime.now().minusHours(hours);

        List<MinecraftServer> servers =
                serverRepository.findAll();

        List<ServerStatusHistory> allHistory =
                historyRepository.findAllSince(since);

        Map<Long, List<ServerStatusHistory>> byServer =
                allHistory.stream()
                        .collect(Collectors.groupingBy(
                                h -> h.getServer().getId()
                        ));

        long onlineServers = servers.stream()
                .filter(server -> {
                    List<ServerStatusHistory> serverHistory =
                            byServer.getOrDefault(
                                    server.getId(),
                                    List.of()
                            );
                    return serverHistory.stream()
                            .findFirst()
                            .map(ServerStatusHistory::isOnline)
                            .orElse(false);
                })
                .count();

        long offlineServers = servers.size() - onlineServers;

        long totalChecks = allHistory.size();
        long onlineChecks = allHistory.stream()
                .filter(ServerStatusHistory::isOnline)
                .count();

        double uptimePercentage =
                totalChecks == 0
                        ? 0.0
                        : (onlineChecks * 100.0) / totalChecks;

        return new AggregateStatisticsDTO(
                servers.size(),
                onlineServers,
                offlineServers,
                uptimePercentage
        );
    }
}