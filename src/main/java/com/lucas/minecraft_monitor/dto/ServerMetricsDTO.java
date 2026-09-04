package com.lucas.minecraft_monitor.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ServerMetricsDTO(
        Long serverId,
        Integer hours,
        Integer minutes,
        List<MetricPointDTO> data
) {

    public record MetricPointDTO(
            LocalDateTime checkedAt,
            boolean online,
            int players,
            long latency
    ) {
    }
}