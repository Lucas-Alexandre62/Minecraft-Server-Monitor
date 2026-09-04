package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.repository.ServerEventRepository;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {

    private final ServerEventRepository serverEventRepository;
    private final AlertService alertService;

    public MonitoringService(
            ServerEventRepository serverEventRepository,
            AlertService alertService
    ) {
        this.serverEventRepository = serverEventRepository;
        this.alertService = alertService;
    }

    public void processStatus(
            MinecraftServer server,
            boolean currentOnline
    ) {

        ServerEvent previousEvent =
                serverEventRepository
                        .findTopByServerOrderByCreatedAtDesc(server)
                        .orElse(null);

        // Primeira verificação do servidor
        if (previousEvent == null) {

            ServerEvent.EventType initialEvent =
                    currentOnline
                            ? ServerEvent.EventType.SERVER_UP
                            : ServerEvent.EventType.SERVER_DOWN;

            createEvent(server, initialEvent);

            return;
        }

        boolean previousOnline =
                previousEvent.getType() == ServerEvent.EventType.SERVER_UP;

        // Não houve mudança de estado
        if (previousOnline == currentOnline) {
            return;
        }

        // ONLINE -> OFFLINE
        if (previousOnline && !currentOnline) {

            createEvent(
                    server,
                    ServerEvent.EventType.SERVER_DOWN
            );

            alertService.serverDown(server);

            return;
        }

        // OFFLINE -> ONLINE
        if (!previousOnline && currentOnline) {

            createEvent(
                    server,
                    ServerEvent.EventType.SERVER_UP
            );

            alertService.serverUp(server);
        }
    }

    private void createEvent(
            MinecraftServer server,
            ServerEvent.EventType type
    ) {

        ServerEvent event =
                new ServerEvent(server, type);

        serverEventRepository.save(event);
    }
}