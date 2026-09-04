package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.ServerStatisticsDTO;
import com.lucas.minecraft_monitor.service.ServerStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
public class ServerStatisticsController {

    private final ServerStatisticsService statisticsService;

    public ServerStatisticsController(
            ServerStatisticsService statisticsService
    ) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{serverId}/statistics")
    public ResponseEntity<ServerStatisticsDTO> getStatistics(
            @PathVariable Long serverId,
            @RequestParam(defaultValue = "24") int hours
    ) {

        if (hours <= 0) {
            throw new IllegalArgumentException(
                    "O número de horas deve ser maior que zero."
            );
        }

        return ResponseEntity.ok(
                statisticsService.calculate(
                        serverId,
                        hours
                )
        );
    }
}