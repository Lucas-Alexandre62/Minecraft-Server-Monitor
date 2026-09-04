package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import com.lucas.minecraft_monitor.repository.ServerEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerEventServiceTest {

    @Mock
    private ServerEventRepository eventRepository;

    @Mock
    private MinecraftServerRepository serverRepository;

    @InjectMocks
    private ServerEventService eventService;

    private MinecraftServer createServer() {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);
        server.setName("Test Server");
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    @Test
    void findEventsReturnsEventsForServer() {
        MinecraftServer server = createServer();
        Pageable pageable = PageRequest.of(0, 10);

        when(serverRepository.findById(1L))
                .thenReturn(Optional.of(server));

        ServerEvent event = new ServerEvent(
                server,
                ServerEvent.EventType.SERVER_UP
        );

        Page<ServerEvent> page =
                new PageImpl<>(List.of(event), pageable, 1);

        when(eventRepository
                .findByServerOrderByCreatedAtDesc(
                        server, pageable))
                .thenReturn(page);

        Page<ServerEvent> result =
                eventService.findEvents(1L, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(
                ServerEvent.EventType.SERVER_UP,
                result.getContent().get(0).getType()
        );
    }

    @Test
    void findEventsFiltersByType() {
        MinecraftServer server = createServer();
        Pageable pageable = PageRequest.of(0, 10);

        when(serverRepository.findById(1L))
                .thenReturn(Optional.of(server));

        Page<ServerEvent> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(eventRepository
                .findByServerAndTypeOrderByCreatedAtDesc(
                        server,
                        ServerEvent.EventType.SERVER_DOWN,
                        pageable))
                .thenReturn(emptyPage);

        Page<ServerEvent> result = eventService.findEvents(
                1L,
                ServerEvent.EventType.SERVER_DOWN,
                pageable
        );

        assertEquals(0, result.getTotalElements());
        verify(eventRepository)
                .findByServerAndTypeOrderByCreatedAtDesc(
                        any(), any(), any());
    }

    @Test
    void findEventsThrowsWhenServerNotFound() {
        when(serverRepository.findById(99L))
                .thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findEvents(
                        99L, null, pageable)
        );
    }
}
