package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.service.MinecraftServerMonitorService;
import com.lucas.minecraft_monitor.service.MinecraftServerService;
import com.lucas.minecraft_monitor.dto.MinecraftServerStatusDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MinecraftServerController.class)
class MinecraftServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MinecraftServerService service;

    @MockitoBean
    private MinecraftServerMonitorService monitorService;

    private MinecraftServer createServer() {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);
        server.setName("Test Server");
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    @Test
    void findAllReturnsListOfServers() throws Exception {
        MinecraftServer server = createServer();

        when(service.findAll())
                .thenReturn(List.of(server));

        mockMvc.perform(get("/api/servers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Test Server"))
                .andExpect(jsonPath("$[0].host")
                        .value("localhost"))
                .andExpect(jsonPath("$[0].port")
                        .value(25565));
    }

    @Test
    void findByIdReturnsServer() throws Exception {
        MinecraftServer server = createServer();

        when(service.findById(1L))
                .thenReturn(server);

        mockMvc.perform(get("/api/servers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Test Server"));
    }

    @Test
    void findByIdThrowsWhenNotFound() throws Exception {
        when(service.findById(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Servidor não encontrado"));

        mockMvc.perform(get("/api/servers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsCreatedServer() throws Exception {
        MinecraftServer server = createServer();

        when(service.create(any()))
                .thenReturn(server);

        mockMvc.perform(post("/api/servers")
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Test Server",
                                    "host": "localhost",
                                    "port": 25565
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Test Server"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/servers/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void checkStatusReturnsStatus() throws Exception {
        MinecraftServer server = createServer();

        when(service.findById(1L))
                .thenReturn(server);

        MinecraftServerStatusDTO status =
                new MinecraftServerStatusDTO(
                        true, "localhost", 25565, 5, 20,
                        "1.20.4", 45L, "A Minecraft Server"
                );

        when(monitorService.checkServer(
                anyString(), anyInt()))
                .thenReturn(status);

        mockMvc.perform(get("/api/servers/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.online")
                        .value(true))
                .andExpect(jsonPath("$.version")
                        .value("1.20.4"));
    }
}
