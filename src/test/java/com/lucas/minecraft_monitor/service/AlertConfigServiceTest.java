package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.repository.AlertConfigRepository;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertConfigServiceTest {

    @Mock
    private AlertConfigRepository alertConfigRepository;

    @Mock
    private MinecraftServerRepository serverRepository;

    @InjectMocks
    private AlertConfigService alertConfigService;

    private MinecraftServer createServer() {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);
        server.setName("Test Server");
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    @Test
    void findByServerReturnsConfigs() {
        MinecraftServer server = createServer();

        when(serverRepository.findById(1L))
                .thenReturn(Optional.of(server));

        AlertConfig config = new AlertConfig(
                server,
                AlertChannel.LOG,
                true
        );

        when(alertConfigRepository.findByServer(server))
                .thenReturn(List.of(config));

        List<AlertConfig> result =
                alertConfigService.findByServer(1L);

        assertEquals(1, result.size());
        assertEquals(
                AlertChannel.LOG,
                result.get(0).getChannel()
        );
    }

    @Test
    void findByServerThrowsWhenNotFound() {
        when(serverRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> alertConfigService.findByServer(99L)
        );
    }

    @Test
    void upsertCreatesNewConfig() {
        MinecraftServer server = createServer();

        when(serverRepository.findById(1L))
                .thenReturn(Optional.of(server));

        when(alertConfigRepository
                .findByServerAndChannel(
                        server, AlertChannel.WEBHOOK))
                .thenReturn(Optional.empty());

        when(alertConfigRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        AlertConfig result = alertConfigService.upsert(
                1L,
                AlertChannel.WEBHOOK,
                true,
                "https://example.com/hook"
        );

        assertEquals(
                AlertChannel.WEBHOOK,
                result.getChannel()
        );
        assertTrue(result.isEnabled());
        assertEquals(
                "https://example.com/hook",
                result.getUrl()
        );
    }

    @Test
    void upsertUpdatesExistingConfig() {
        MinecraftServer server = createServer();

        when(serverRepository.findById(1L))
                .thenReturn(Optional.of(server));

        AlertConfig existing = new AlertConfig(
                server,
                AlertChannel.LOG,
                true
        );

        when(alertConfigRepository
                .findByServerAndChannel(
                        server, AlertChannel.LOG))
                .thenReturn(Optional.of(existing));

        when(alertConfigRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        AlertConfig result = alertConfigService.upsert(
                1L,
                AlertChannel.LOG,
                false
        );

        assertFalse(result.isEnabled());
    }

    @Test
    void initializeDefaultsCreatesAllChannels() {
        MinecraftServer server = createServer();

        when(serverRepository.findById(1L))
                .thenReturn(Optional.of(server));

        when(alertConfigRepository
                .findByServerAndChannel(
                        any(), any()))
                .thenReturn(Optional.empty());

        when(alertConfigRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        alertConfigService.initializeDefaults(1L);

        verify(alertConfigRepository, times(3))
                .save(any());
    }
}
