package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.repository.AlertConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompositeAlertService implements AlertService {

    private final AlertConfigRepository alertConfigRepository;
    private final LogAlertService logAlertService;
    private final WebhookAlertService webhookAlertService;

    public CompositeAlertService(
            AlertConfigRepository alertConfigRepository,
            LogAlertService logAlertService,
            WebhookAlertService webhookAlertService
    ) {
        this.alertConfigRepository = alertConfigRepository;
        this.logAlertService = logAlertService;
        this.webhookAlertService = webhookAlertService;
    }

    @Override
    public void serverDown(MinecraftServer server) {
        List<AlertConfig> configs =
                alertConfigRepository.findByServerAndEnabledTrue(server);

        for (AlertConfig config : configs) {
            switch (config.getChannel()) {
                case LOG -> logAlertService.serverDown(server);
                case WEBHOOK -> webhookAlertService.serverDown(server);
                case EMAIL -> {}
            }
        }
    }

    @Override
    public void serverUp(MinecraftServer server) {
        List<AlertConfig> configs =
                alertConfigRepository.findByServerAndEnabledTrue(server);

        for (AlertConfig config : configs) {
            switch (config.getChannel()) {
                case LOG -> logAlertService.serverUp(server);
                case WEBHOOK -> webhookAlertService.serverUp(server);
                case EMAIL -> {}
            }
        }
    }
}
