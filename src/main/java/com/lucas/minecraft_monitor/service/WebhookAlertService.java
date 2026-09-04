package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.repository.AlertConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class WebhookAlertService {

    private static final Logger logger =
            LoggerFactory.getLogger(WebhookAlertService.class);

    private final AlertConfigRepository alertConfigRepository;
    private final HttpClient httpClient;

    public WebhookAlertService(
            AlertConfigRepository alertConfigRepository
    ) {
        this.alertConfigRepository = alertConfigRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void serverDown(MinecraftServer server) {
        sendWebhooks(server, "SERVER_DOWN",
                "Servidor OFFLINE: " + server.getName()
                        + " (" + server.getHost()
                        + ":" + server.getPort() + ")");
    }

    public void serverUp(MinecraftServer server) {
        sendWebhooks(server, "SERVER_UP",
                "Servidor ONLINE novamente: " + server.getName()
                        + " (" + server.getHost()
                        + ":" + server.getPort() + ")");
    }

    private void sendWebhooks(
            MinecraftServer server,
            String eventType,
            String message
    ) {
        List<AlertConfig> configs =
                alertConfigRepository.findByServerAndEnabledTrue(server);

        for (AlertConfig config : configs) {
            if (config.getChannel() != AlertChannel.WEBHOOK) {
                continue;
            }

            String url = config.getUrl();

            if (url == null || url.isBlank()) {
                logger.warn(
                        "Webhook configurado sem URL para servidor {}",
                        server.getName()
                );
                continue;
            }

            sendPost(url, eventType, message, server.getName());
        }
    }

    private void sendPost(
            String url,
            String eventType,
            String message,
            String serverName
    ) {
        try {
            String json = """
                    {
                        "event": "%s",
                        "server": "%s",
                        "message": "%s"
                    }
                    """.formatted(
                    eventType,
                    escapeJson(serverName),
                    escapeJson(message)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            httpClient.sendAsync(request,
                            HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 400) {
                            logger.warn(
                                    "Webhook returned {} for server {}",
                                    response.statusCode(),
                                    serverName
                            );
                        }
                    })
                    .exceptionally(ex -> {
                        logger.error(
                                "Erro ao enviar webhook para {}: {}",
                                url,
                                ex.getMessage()
                        );
                        return null;
                    });
        } catch (Exception e) {
            logger.error(
                    "Erro ao enviar webhook para {}: {}",
                    url,
                    e.getMessage()
            );
        }
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
