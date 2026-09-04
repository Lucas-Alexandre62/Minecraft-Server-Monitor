package com.lucas.minecraft_monitor.scheduler;

import com.lucas.minecraft_monitor.dto.MinecraftServerStatusDTO;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import com.lucas.minecraft_monitor.service.MinecraftServerMonitorService;
import com.lucas.minecraft_monitor.service.MinecraftServerService;
import com.lucas.minecraft_monitor.service.ServerStatusHistoryService;
import com.lucas.minecraft_monitor.service.MonitoringService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MinecraftServerMonitoringScheduler {

    private final MinecraftServerService serverService;
    private final MinecraftServerMonitorService monitorService;
    private final ServerStatusHistoryService historyService;
    private final MonitoringService monitoringService;

    public MinecraftServerMonitoringScheduler(
            MinecraftServerService serverService,
            MinecraftServerMonitorService monitorService,
            ServerStatusHistoryService historyService,
            MonitoringService monitoringService
    ) {
        this.serverService = serverService;
        this.monitorService = monitorService;
        this.historyService = historyService;
        this.monitoringService = monitoringService;
    }

    @Scheduled(fixedRate = 30000)
    public void monitorServers() {

        System.out.println(
                "Iniciando verificação dos servidores..."
        );

        for (MinecraftServer server :
                serverService.findAll()) {

            MinecraftServerStatusDTO status =
                    monitorService.checkServer(
                            server.getHost(),
                            server.getPort()
                    );

            System.out.println(
                    server.getName()
                            + " -> "
                            + (status.online()
                            ? "ONLINE"
                            : "OFFLINE")
                            + " | Jogadores: "
                            + status.playersOnline()
                            + " | Latência: "
                            + status.latency()
                            + " ms"
            );

            ServerStatusHistory history =
                    new ServerStatusHistory(
                            server,
                            status.online(),
                            status.playersOnline(),
                            status.maxPlayers(),
                            status.latency(),
                            status.version(),
                            LocalDateTime.now()
                    );

            historyService.save(history);

            monitoringService.processStatus(
                    server,
                    status.online()
            );
        }
    }
}