package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.service.ServerEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServerEventController.class)
class ServerEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServerEventService eventService;

    private ServerEvent createEvent(
            ServerEvent.EventType type
    ) {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);

        ServerEvent event = new ServerEvent(server, type);
        ReflectionTestUtils.setField(event, "id", 1L);
        ReflectionTestUtils.setField(
                event, "createdAt", LocalDateTime.now());
        return event;
    }

    @Test
    void getEventsReturnsPaginatedEvents() throws Exception {
        ServerEvent event =
                createEvent(ServerEvent.EventType.SERVER_UP);

        Pageable pageable = PageRequest.of(0, 20);

        when(eventService.findEvents(
                any(), any(), any()))
                .thenReturn(
                        new PageImpl<>(
                                List.of(event), pageable, 1));

        mockMvc.perform(
                        get("/api/servers/1/events"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].type")
                                .value("SERVER_UP"))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1));
    }

    @Test
    void getEventsWithNegativePageReturns400()
            throws Exception {
        mockMvc.perform(
                        get("/api/servers/1/events")
                                .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEventsWithInvalidSizeReturns400()
            throws Exception {
        mockMvc.perform(
                        get("/api/servers/1/events")
                                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEventsWithSizeOver100Returns400()
            throws Exception {
        mockMvc.perform(
                        get("/api/servers/1/events")
                                .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEventsByTypeReturnsFilteredEvents()
            throws Exception {
        ServerEvent event = createEvent(
                ServerEvent.EventType.SERVER_DOWN);

        Pageable pageable = PageRequest.of(0, 20);

        when(eventService.findEvents(
                any(), any(), any()))
                .thenReturn(
                        new PageImpl<>(
                                List.of(event), pageable, 1));

        mockMvc.perform(
                        get("/api/servers/1/events")
                                .param("type", "SERVER_DOWN"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].type")
                                .value("SERVER_DOWN"));
    }
}
