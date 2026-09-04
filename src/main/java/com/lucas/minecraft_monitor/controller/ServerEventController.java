package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.ServerEventDTO;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.service.ServerEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        return eventService
                .findEvents(serverId, type, pageable)
                .map(ServerEventDTO::fromEntity);
    }
}