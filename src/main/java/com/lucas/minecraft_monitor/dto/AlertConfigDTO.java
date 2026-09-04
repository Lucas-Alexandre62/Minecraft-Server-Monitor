package com.lucas.minecraft_monitor.dto;

import com.lucas.minecraft_monitor.model.AlertConfig;

public record AlertConfigDTO(
        Long id,
        Long serverId,
        String channel,
        boolean enabled,
        String url
) {
    public static AlertConfigDTO fromEntity(AlertConfig config) {
        return new AlertConfigDTO(
                config.getId(),
                config.getServer().getId(),
                config.getChannel().name(),
                config.isEnabled(),
                config.getUrl()
        );
    }
}
