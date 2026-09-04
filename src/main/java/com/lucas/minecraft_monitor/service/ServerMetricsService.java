package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.dto.ServerMetricsDTO;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServerMetricsService {

    private final ServerStatusHistoryService historyService;

    public ServerMetricsService(
            ServerStatusHistoryService historyService
    ) {
        this.historyService = historyService;
    }

    public ServerMetricsDTO getMetrics(
            Long serverId,
            int hours
    ) {

        LocalDateTime since =
                LocalDateTime.now().minusHours(hours);

        List<ServerStatusHistory> history =
                historyService.findMetrics(
                        serverId,
                        since
                );

        List<ServerMetricsDTO.MetricPointDTO> data =
                history.stream()
                        .map(entry ->
                                new ServerMetricsDTO.MetricPointDTO(
                                        entry.getCheckedAt(),
                                        entry.isOnline(),
                                        entry.getPlayersOnline(),
                                        entry.getLatency()
                                )
                        )
                        .toList();

        return new ServerMetricsDTO(
                serverId,
                hours,
                null,
                data
        );
    }

    public ServerMetricsDTO getMetricsByMinutes(
            Long serverId,
            int minutes
    ) {

        LocalDateTime since =
                LocalDateTime.now().minusMinutes(minutes);

        List<ServerStatusHistory> history =
                historyService.findMetrics(
                        serverId,
                        since
                );

        List<ServerMetricsDTO.MetricPointDTO> data =
                history.stream()
                        .map(entry ->
                                new ServerMetricsDTO.MetricPointDTO(
                                        entry.getCheckedAt(),
                                        entry.isOnline(),
                                        entry.getPlayersOnline(),
                                        entry.getLatency()
                                )
                        )
                        .toList();

        return new ServerMetricsDTO(
                serverId,
                null,
                minutes,
                data
        );
    }
}