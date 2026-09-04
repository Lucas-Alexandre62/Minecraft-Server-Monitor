package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.AlertConfigDTO;
import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.service.AlertConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class AlertConfigController {

    private final AlertConfigService alertConfigService;

    public AlertConfigController(
            AlertConfigService alertConfigService
    ) {
        this.alertConfigService = alertConfigService;
    }

    @GetMapping("/{serverId}/alerts")
    public List<AlertConfigDTO> getAlertConfigs(
            @PathVariable Long serverId
    ) {
        return alertConfigService
                .findByServer(serverId)
                .stream()
                .map(AlertConfigDTO::fromEntity)
                .toList();
    }

    @PutMapping("/{serverId}/alerts")
    public AlertConfigDTO updateAlertConfig(
            @PathVariable Long serverId,
            @RequestBody AlertConfigRequest request
    ) {
        AlertChannel channel = AlertChannel.valueOf(
                request.channel()
        );

        AlertConfig config = alertConfigService.upsert(
                serverId,
                channel,
                request.enabled()
        );

        return AlertConfigDTO.fromEntity(config);
    }

    public record AlertConfigRequest(
            String channel,
            boolean enabled
    ) {
    }
}
