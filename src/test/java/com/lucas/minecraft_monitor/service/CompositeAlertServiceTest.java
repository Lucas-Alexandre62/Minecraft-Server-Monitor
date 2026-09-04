package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.repository.AlertConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeAlertServiceTest {

    @Mock
    private AlertConfigRepository alertConfigRepository;

    @Mock
    private LogAlertService logAlertService;

    @Mock
    private WebhookAlertService webhookAlertService;

    @InjectMocks
    private CompositeAlertService compositeAlertService;

    private MinecraftServer createServer() {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);
        server.setName("Test Server");
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    @Test
    void serverDownDelegatesToLogAndWebhook() {
        MinecraftServer server = createServer();

        AlertConfig logConfig = new AlertConfig(
                server, AlertChannel.LOG, true);
        AlertConfig webhookConfig = new AlertConfig(
                server, AlertChannel.WEBHOOK, true);

        when(alertConfigRepository
                .findByServerAndEnabledTrue(server))
                .thenReturn(List.of(logConfig, webhookConfig));

        compositeAlertService.serverDown(server);

        verify(logAlertService).serverDown(server);
        verify(webhookAlertService).serverDown(server);
    }

    @Test
    void serverUpDelegatesToLogAndWebhook() {
        MinecraftServer server = createServer();

        AlertConfig logConfig = new AlertConfig(
                server, AlertChannel.LOG, true);

        when(alertConfigRepository
                .findByServerAndEnabledTrue(server))
                .thenReturn(List.of(logConfig));

        compositeAlertService.serverUp(server);

        verify(logAlertService).serverUp(server);
        verifyNoInteractions(webhookAlertService);
    }

    @Test
    void noEnabledConfigsDoesNotDelegate() {
        MinecraftServer server = createServer();

        when(alertConfigRepository
                .findByServerAndEnabledTrue(server))
                .thenReturn(List.of());

        compositeAlertService.serverDown(server);

        verifyNoInteractions(logAlertService);
        verifyNoInteractions(webhookAlertService);
    }

    @Test
    void disabledChannelsAreNotReturnedByRepository() {
        MinecraftServer server = createServer();

        when(alertConfigRepository
                .findByServerAndEnabledTrue(server))
                .thenReturn(List.of());

        compositeAlertService.serverDown(server);

        verifyNoInteractions(logAlertService);
        verifyNoInteractions(webhookAlertService);
    }
}
