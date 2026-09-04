package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.ServerEventDTO;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.service.ServerEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
public class ServerEventController {

    private final ServerEventService eventService;

    public ServerEventController(
            ServerEventService eventService
    ) {
        this.eventService = eventService;
    }

    @GetMapping("/{serverId}/events")
    public Page<ServerEventDTO> getEvents(
            @PathVariable Long serverId,
            @RequestParam(required = false)
            ServerEvent.EventType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page deve ser maior ou igual a 0 e size deve estar entre 1 e 100"
            );
        }

        return eventService
                .findEvents(serverId, type, PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                ))
                .map(ServerEventDTO::fromEntity);
    }
}
