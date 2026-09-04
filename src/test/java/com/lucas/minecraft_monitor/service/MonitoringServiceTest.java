package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.repository.ServerEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoringServiceTest {

    @Mock
    private ServerEventRepository serverEventRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private MonitoringService monitoringService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                monitoringService, "latencyThreshold", 500L
        );
    }

    private MinecraftServer createServer() {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);
        server.setName("Test Server");
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    @Test
    void firstCheckOnlineCreatesServerUpEvent() {
        MinecraftServer server = createServer();

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.empty());

        monitoringService.processStatus(server, true, 50);

        ArgumentCaptor<ServerEvent> captor =
                ArgumentCaptor.forClass(ServerEvent.class);

        verify(serverEventRepository).save(captor.capture());

        ServerEvent saved = captor.getValue();
        assertEquals(
                ServerEvent.EventType.SERVER_UP,
                saved.getType()
        );
        assertEquals(server, saved.getServer());
    }

    @Test
    void firstCheckOfflineCreatesServerDownEvent() {
        MinecraftServer server = createServer();

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.empty());

        monitoringService.processStatus(server, false, 0);

        ArgumentCaptor<ServerEvent> captor =
                ArgumentCaptor.forClass(ServerEvent.class);

        verify(serverEventRepository).save(captor.capture());

        assertEquals(
                ServerEvent.EventType.SERVER_DOWN,
                captor.getValue().getType()
        );
    }

    @Test
    void firstCheckDoesNotTriggerAlert() {
        MinecraftServer server = createServer();

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.empty());

        monitoringService.processStatus(server, true, 50);

        verifyNoInteractions(alertService);
    }

    @Test
    void onlineToOnlineWithLowLatencyDoesNotCreateEvent() {
        MinecraftServer server = createServer();

        ServerEvent previous = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_UP
        );

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.of(previous));

        monitoringService.processStatus(server, true, 50);

        verify(serverEventRepository, never()).save(any());
        verifyNoInteractions(alertService);
    }

    @Test
    void offlineToOfflineDoesNotCreateEvent() {
        MinecraftServer server = createServer();

        ServerEvent previous = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_DOWN
        );

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.of(previous));

        monitoringService.processStatus(server, false, 0);

        verify(serverEventRepository, never()).save(any());
        verifyNoInteractions(alertService);
    }

    @Test
    void onlineToOfflineCreatesDownEventAndAlert() {
        MinecraftServer server = createServer();

        ServerEvent previous = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_UP
        );

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.of(previous));

        monitoringService.processStatus(server, false, 0);

        ArgumentCaptor<ServerEvent> captor =
                ArgumentCaptor.forClass(ServerEvent.class);

        verify(serverEventRepository).save(captor.capture());

        assertEquals(
                ServerEvent.EventType.SERVER_DOWN,
                captor.getValue().getType()
        );
        verify(alertService).serverDown(server);
    }

    @Test
    void offlineToOnlineCreatesUpEventAndAlert() {
        MinecraftServer server = createServer();

        ServerEvent previous = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_DOWN
        );

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.of(previous));

        monitoringService.processStatus(server, true, 50);

        ArgumentCaptor<ServerEvent> captor =
                ArgumentCaptor.forClass(ServerEvent.class);

        verify(serverEventRepository).save(captor.capture());

        assertEquals(
                ServerEvent.EventType.SERVER_UP,
                captor.getValue().getType()
        );
        verify(alertService).serverUp(server);
    }

    @Test
    void highLatencyCreatesHighLatencyEvent() {
        MinecraftServer server = createServer();

        ServerEvent previous = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_UP
        );

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.of(previous));

        when(serverEventRepository
                .findTopByServerOrderByCreatedAtDesc(server))
                .thenReturn(Optional.of(previous));

        monitoringService.processStatus(server, true, 1000);

        ArgumentCaptor<ServerEvent> captor =
                ArgumentCaptor.forClass(ServerEvent.class);

        verify(serverEventRepository).save(captor.capture());

        assertEquals(
                ServerEvent.EventType.HIGH_LATENCY,
                captor.getValue().getType()
        );
    }

    @Test
    void highLatencySpamPreventedByLastEventCheck() {
        MinecraftServer server = createServer();

        ServerEvent previousState = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_UP
        );

        ServerEvent lastHighLatency = new ServerEvent(
                server,
                ServerEvent.EventType.HIGH_LATENCY
        );

        when(serverEventRepository
                .findTopStateEventByServer(server))
                .thenReturn(Optional.of(previousState));

        when(serverEventRepository
                .findTopByServerOrderByCreatedAtDesc(server))
                .thenReturn(Optional.of(lastHighLatency));

        monitoringService.processStatus(server, true, 1000);

        verify(serverEventRepository, never()).save(any());
    }
}
