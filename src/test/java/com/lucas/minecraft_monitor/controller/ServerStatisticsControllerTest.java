package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.AggregateStatisticsDTO;
import com.lucas.minecraft_monitor.dto.ServerStatisticsDTO;
import com.lucas.minecraft_monitor.service.ServerStatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServerStatisticsController.class)
class ServerStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServerStatisticsService statisticsService;

    @Test
    void getStatisticsReturnsData() throws Exception {
        ServerStatisticsDTO dto = new ServerStatisticsDTO(
                24, 100, 90, 10,
                90.0, 5.0, 12, 45.0
        );

        when(statisticsService.calculate(anyLong(), anyInt()))
                .thenReturn(dto);

        mockMvc.perform(
                        get("/api/servers/1/statistics"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.periodHours").value(24))
                .andExpect(
                        jsonPath("$.uptimePercentage")
                                .value(90.0));
    }

    @Test
    void getStatisticsWithZeroHoursReturns400()
            throws Exception {
        mockMvc.perform(
                        get("/api/servers/1/statistics")
                                .param("hours", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAggregateStatisticsReturnsData()
            throws Exception {
        AggregateStatisticsDTO dto =
                new AggregateStatisticsDTO(
                        3, 2, 1, 85.5
                );

        when(statisticsService.calculateAggregate(anyInt()))
                .thenReturn(dto);

        mockMvc.perform(
                        get("/api/servers/statistics"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalServers").value(3))
                .andExpect(
                        jsonPath("$.onlineServers").value(2))
                .andExpect(
                        jsonPath("$.uptimePercentage")
                                .value(85.5));
    }

    @Test
    void getAggregateWithZeroHoursReturns400()
            throws Exception {
        mockMvc.perform(
                        get("/api/servers/statistics")
                                .param("hours", "-1"))
                .andExpect(status().isBadRequest());
    }
}
