package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.dto.AggregateStatisticsDTO;
import com.lucas.minecraft_monitor.dto.ServerStatisticsDTO;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import com.lucas.minecraft_monitor.repository.ServerStatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerStatisticsServiceTest {

    @Mock
    private ServerStatusHistoryService historyService;

    @Mock
    private ServerStatusHistoryRepository historyRepository;

    @Mock
    private MinecraftServerRepository serverRepository;

    @InjectMocks
    private ServerStatisticsService statisticsService;

    private MinecraftServer createServer(Long id) {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", id);
        server.setName("Server " + id);
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    private ServerStatusHistory createHistoryEntry(
            MinecraftServer server,
            boolean online,
            int players,
            long latency
    ) {
        ServerStatusHistory entry = new ServerStatusHistory();
        entry.setServer(server);
        entry.setOnline(online);
        entry.setPlayersOnline(players);
        entry.setLatency(latency);
        return entry;
    }

    @Test
    void calculateWithEmptyHistoryReturnsZeros() {
        when(historyService.findEntitiesByServerId(
                any(), any()))
                .thenReturn(List.of());

        ServerStatisticsDTO result =
                statisticsService.calculate(1L, 24);

        assertEquals(24, result.periodHours());
        assertEquals(0, result.totalChecks());
        assertEquals(0, result.onlineChecks());
        assertEquals(0, result.offlineChecks());
        assertEquals(0.0, result.uptimePercentage());
        assertEquals(0.0, result.averagePlayers());
        assertEquals(0, result.peakPlayers());
        assertEquals(0.0, result.averageLatency());
    }

    @Test
    void calculateWithMixedHistory() {
        MinecraftServer server = createServer(1L);

        List<ServerStatusHistory> history = List.of(
                createHistoryEntry(server, true, 5, 50),
                createHistoryEntry(server, true, 10, 100),
                createHistoryEntry(server, false, 0, 0),
                createHistoryEntry(server, true, 3, 75)
        );

        when(historyService.findEntitiesByServerId(
                any(), any()))
                .thenReturn(history);

        ServerStatisticsDTO result =
                statisticsService.calculate(1L, 24);

        assertEquals(4, result.totalChecks());
        assertEquals(3, result.onlineChecks());
        assertEquals(1, result.offlineChecks());
        assertEquals(75.0, result.uptimePercentage(), 0.01);
        assertEquals(4.5, result.averagePlayers(), 0.01);
        assertEquals(10, result.peakPlayers());
        assertEquals(
                75.0,
                result.averageLatency(),
                0.01
        );
    }

    @Test
    void calculateAggregateWithEmptyData() {
        when(serverRepository.findAll())
                .thenReturn(List.of());
        when(historyRepository.findAllSince(any()))
                .thenReturn(List.of());

        AggregateStatisticsDTO result =
                statisticsService.calculateAggregate(24);

        assertEquals(0, result.totalServers());
        assertEquals(0, result.onlineServers());
        assertEquals(0, result.offlineServers());
        assertEquals(0.0, result.uptimePercentage());
    }

    @Test
    void calculateAggregateWithMultipleServers() {
        MinecraftServer server1 = createServer(1L);
        MinecraftServer server2 = createServer(2L);

        when(serverRepository.findAll())
                .thenReturn(List.of(server1, server2));

        ServerStatusHistory entry1 =
                createHistoryEntry(server1, true, 5, 50);
        ServerStatusHistory entry2 =
                createHistoryEntry(server2, false, 0, 0);

        when(historyRepository.findAllSince(any()))
                .thenReturn(List.of(entry1, entry2));

        AggregateStatisticsDTO result =
                statisticsService.calculateAggregate(24);

        assertEquals(2, result.totalServers());
        assertEquals(1, result.onlineServers());
        assertEquals(1, result.offlineServers());
        assertEquals(50.0, result.uptimePercentage(), 0.01);
    }
}
