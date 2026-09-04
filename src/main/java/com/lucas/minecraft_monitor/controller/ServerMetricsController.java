package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.ServerMetricsDTO;
import com.lucas.minecraft_monitor.service.ServerMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
public class ServerMetricsController {

    private final ServerMetricsService metricsService;

    public ServerMetricsController(
            ServerMetricsService metricsService
    ) {
        this.metricsService = metricsService;
    }

    @GetMapping("/{serverId}/metrics")
    public ResponseEntity<ServerMetricsDTO> getMetrics(
            @PathVariable Long serverId,
            @RequestParam(required = false) Integer minutes,
            @RequestParam(required = false) Integer hours
    ) {
        if (minutes != null && minutes <= 0) {
            throw new IllegalArgumentException(
                    "O valor de minutes deve ser maior que 0"
            );
        }

        if (hours != null && hours <= 0) {
            throw new IllegalArgumentException(
                    "O valor de hours deve ser maior que 0"
            );
        }

        if (minutes != null && hours != null) {
            throw new IllegalArgumentException(
                    "Informe apenas minutes ou hours"
            );
        }

        if (minutes != null) {
            return ResponseEntity.ok(
                    metricsService.getMetricsByMinutes(
                            serverId,
                            minutes
                    )
            );
        }

        int requestedHours = hours != null ? hours : 1;

        return ResponseEntity.ok(
                metricsService.getMetrics(
                        serverId,
                        requestedHours
                )
        );
    }
}
