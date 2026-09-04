package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.ServerStatusHistoryDTO;
import com.lucas.minecraft_monitor.service.ServerStatusHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page deve ser maior ou igual a 0 e size deve estar entre 1 e 100"
            );
        }

        return historyService
                .findHistory(serverId, PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "checkedAt")
                ))
                .map(ServerStatusHistoryDTO::fromEntity);
    }
}
