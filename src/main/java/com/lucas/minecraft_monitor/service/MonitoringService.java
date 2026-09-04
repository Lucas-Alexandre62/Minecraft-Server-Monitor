package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.repository.ServerEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {

    private final ServerEventRepository serverEventRepository;
    private final AlertService alertService;

    @Value("${alert.latency.threshold:500}")
    private long latencyThreshold;

    public MonitoringService(
            ServerEventRepository serverEventRepository,
            AlertService alertService
    ) {
        this.serverEventRepository = serverEventRepository;
        this.alertService = alertService;
    }

    public void processStatus(
            MinecraftServer server,
            boolean currentOnline,
            long latency
    ) {

        ServerEvent previousStateEvent =
                serverEventRepository
                        .findTopStateEventByServer(server)
                        .orElse(null);

        boolean previousOnline =
                previousStateEvent != null
                && previousStateEvent.getType()
                        == ServerEvent.EventType.SERVER_UP;

        if (previousStateEvent == null) {
            ServerEvent.EventType initialEvent =
                    currentOnline
                            ? ServerEvent.EventType.SERVER_UP
                            : ServerEvent.EventType.SERVER_DOWN;

            createEvent(server, initialEvent);

            if (currentOnline && latency > latencyThreshold) {
                createEvent(
                        server,
                        ServerEvent.EventType.HIGH_LATENCY
                );
            }

            return;
        }

        // Estado de online/offline
        if (previousOnline != currentOnline) {
            if (previousOnline && !currentOnline) {
                createEvent(
                        server,
                        ServerEvent.EventType.SERVER_DOWN
                );
                alertService.serverDown(server);
            } else {
                createEvent(
                        server,
                        ServerEvent.EventType.SERVER_UP
                );
                alertService.serverUp(server);
            }
        }

        // Latência elevada (só quando online)
        if (currentOnline && latency > latencyThreshold) {
            if (!isLastEventHighLatency(server)) {
                createEvent(
                        server,
                        ServerEvent.EventType.HIGH_LATENCY
                );
            }
        }
    }

    private boolean isLastEventHighLatency(
            MinecraftServer server
    ) {
        return serverEventRepository
                .findTopByServerOrderByCreatedAtDesc(server)
                .map(event ->
                        event.getType()
                                == ServerEvent.EventType.HIGH_LATENCY
                )
                .orElse(false);
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
