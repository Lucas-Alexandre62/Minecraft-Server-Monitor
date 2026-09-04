package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.service.AlertConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertConfigController.class)
class AlertConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    void getAlertConfigsReturnsList() throws Exception {
        MinecraftServer server = createServer();

        AlertConfig config = new AlertConfig(
                server, AlertChannel.LOG, true);

        when(alertConfigService.findByServer(1L))
                .thenReturn(List.of(config));

        mockMvc.perform(
                        get("/api/servers/1/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].channel")
                        .value("LOG"))
                .andExpect(jsonPath("$[0].enabled")
                        .value(true));
    }

    @Test
    void updateAlertConfigReturnsUpdatedConfig()
            throws Exception {
        MinecraftServer server = createServer();

        AlertConfig config = new AlertConfig(
                server, AlertChannel.WEBHOOK, true);
        config.setUrl("https://example.com/hook");

        when(alertConfigService.upsert(
                anyLong(), any(), anyBoolean(), any()))
                .thenReturn(config);

        mockMvc.perform(
                        put("/api/servers/1/alerts")
                                .contentType(
                                        "application/json")
                                .content("""
                                        {
                                            "channel": "WEBHOOK",
                                            "enabled": true,
                                            "url": "https://example.com/hook"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channel")
                        .value("WEBHOOK"))
                .andExpect(jsonPath("$.enabled")
                        .value(true))
                .andExpect(jsonPath("$.url")
                        .value("https://example.com/hook"));
    }

    @Test
    void getAlertConfigsThrowsWhenServerNotFound()
            throws Exception {
        when(alertConfigService.findByServer(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Servidor não encontrado"));

        mockMvc.perform(
                        get("/api/servers/99/alerts"))
                .andExpect(status().isNotFound());
    }
}
