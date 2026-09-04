package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogAlertService {

    private static final Logger logger =
            LoggerFactory.getLogger(LogAlertService.class);

    public void serverDown(MinecraftServer server) {
        logger.warn("Servidor OFFLINE: {} ({}:{})",
                server.getName(),
                server.getHost(),
                server.getPort());
    }

    public void serverUp(MinecraftServer server) {
        logger.info("Servidor ONLINE novamente: {} ({}:{})",
                server.getName(),
                server.getHost(),
                server.getPort());
    }
}