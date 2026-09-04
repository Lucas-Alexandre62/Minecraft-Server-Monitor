package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.dto.MinecraftPingResponse;
import com.lucas.minecraft_monitor.dto.MinecraftServerStatusDTO;
import org.springframework.stereotype.Service;

@Service
public class MinecraftServerMonitorService {

    private final MinecraftPingService pingService;

    public MinecraftServerMonitorService(
            MinecraftPingService pingService
    ) {
        this.pingService = pingService;
    }

    public MinecraftServerStatusDTO checkServer(
            String host,
            int port
    ) {

        long start =
                System.currentTimeMillis();

        try {

            MinecraftPingResponse response =
                    pingService.ping(host, port);

            long latency =
                    System.currentTimeMillis() - start;

            return new MinecraftServerStatusDTO(
                    true,
                    host,
                    port,
                    response.playersOnline(),
                    response.maxPlayers(),
                    response.version(),
                    latency,
                    response.motd()
            );

        } catch (Exception e) {

            long latency =
                    System.currentTimeMillis() - start;

            return new MinecraftServerStatusDTO(
                    false,
                    host,
                    port,
                    0,
                    0,
                    null,
                    latency,
                    null
            );
        }
    }
}