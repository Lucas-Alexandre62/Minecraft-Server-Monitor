package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.ServerStatusHistoryDTO;
import com.lucas.minecraft_monitor.service.ServerStatusHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
public class ServerStatusHistoryController {

    private final ServerStatusHistoryService historyService;

    public ServerStatusHistoryController(
            ServerStatusHistoryService historyService
    ) {
        this.historyService = historyService;
    }

    @GetMapping("/{serverId}/history")
    public Page<ServerStatusHistoryDTO> getHistory(
            @PathVariable Long serverId,

            @PageableDefault(
                    size = 20,
                    sort = "checkedAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        return historyService
                .findHistory(serverId, pageable)
                .map(ServerStatusHistoryDTO::fromEntity);
    }
}