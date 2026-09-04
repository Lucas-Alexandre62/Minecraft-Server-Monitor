package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.repository.ServerEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MonitoringService {

    private final ServerEventRepository serverEventRepository;
    private final AlertService alertService;

    @Value("${alert.latency.threshold:500}")
    private long latencyThreshold;

    private static final int INSTABILITY_WINDOW_MINUTES = 10;
    private static final int INSTABILITY_THRESHOLD = 3;

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
                        .findTop1ByServerAndTypeInOrderByCreatedAtDesc(
                                server,
                                java.util.List.of(
                                        ServerEvent.EventType.SERVER_UP,
                                        ServerEvent.EventType.SERVER_DOWN
                                )
                        )
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

                checkInstability(server);
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

    private void checkInstability(MinecraftServer server) {
        LocalDateTime since =
                LocalDateTime.now()
                        .minusMinutes(INSTABILITY_WINDOW_MINUTES);

        long downCount =
                serverEventRepository
                        .countDownEventsSince(server, since);

        if (downCount >= INSTABILITY_THRESHOLD) {
            if (!isLastEventInstability(server)) {
                createEvent(
                        server,
                        ServerEvent.EventType.INSTABILITY
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

    private boolean isLastEventInstability(
            MinecraftServer server
    ) {
        return serverEventRepository
                .findTopByServerOrderByCreatedAtDesc(server)
                .map(event ->
                        event.getType()
                                == ServerEvent.EventType.INSTABILITY
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
