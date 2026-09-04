package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.repository.AlertConfigRepository;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertConfigService {

    private final AlertConfigRepository alertConfigRepository;
    private final MinecraftServerRepository serverRepository;

    public AlertConfigService(
            AlertConfigRepository alertConfigRepository,
            MinecraftServerRepository serverRepository
    ) {
        this.alertConfigRepository = alertConfigRepository;
        this.serverRepository = serverRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertConfig> findByServer(Long serverId) {
        MinecraftServer server = serverRepository
                .findById(serverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Servidor não encontrado: " + serverId
                ));

        return alertConfigRepository.findByServer(server);
    }

    @Transactional
    public AlertConfig upsert(
            Long serverId,
            AlertChannel channel,
            boolean enabled
    ) {
        return upsert(serverId, channel, enabled, null);
    }

    @Transactional
    public AlertConfig upsert(
            Long serverId,
            AlertChannel channel,
            boolean enabled,
            String url
    ) {
        MinecraftServer server = serverRepository
                .findById(serverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Servidor não encontrado: " + serverId
                ));

        AlertConfig config = alertConfigRepository
                .findByServerAndChannel(server, channel)
                .orElse(new AlertConfig(server, channel, enabled));

        config.setEnabled(enabled);

        if (url != null) {
            config.setUrl(url);
        }

        return alertConfigRepository.save(config);
    }

    @Transactional
    public void initializeDefaults(Long serverId) {
        MinecraftServer server = serverRepository
                .findById(serverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Servidor não encontrado: " + serverId
                ));

        for (AlertChannel channel : AlertChannel.values()) {
            alertConfigRepository
                    .findByServerAndChannel(server, channel)
                    .orElseGet(() -> alertConfigRepository.save(
                            new AlertConfig(server, channel, true)
                    ));
        }
    }
}
