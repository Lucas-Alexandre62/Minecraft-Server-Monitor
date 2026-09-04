package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import com.lucas.minecraft_monitor.repository.ServerEventRepository;
import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerEventService {

    private final ServerEventRepository eventRepository;
    private final MinecraftServerRepository serverRepository;

    public ServerEventService(
            ServerEventRepository eventRepository,
            MinecraftServerRepository serverRepository
    ) {
        this.eventRepository = eventRepository;
        this.serverRepository = serverRepository;
    }

    @Transactional(readOnly = true)
    public Page<ServerEvent> findEvents(
            Long serverId,
            ServerEvent.EventType type,
            Pageable pageable
    ) {

        MinecraftServer server =
                serverRepository.findById(serverId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Servidor não encontrado"
                                )
                        );

        if (type != null) {

            return eventRepository
                    .findByServerAndTypeOrderByCreatedAtDesc(
                            server,
                            type,
                            pageable
                    );
        }

        return eventRepository
                .findByServerOrderByCreatedAtDesc(
                        server,
                        pageable
                );
    }
}