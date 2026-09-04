package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import com.lucas.minecraft_monitor.repository.ServerStatusHistoryRepository;
import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServerStatusHistoryService {

    private final ServerStatusHistoryRepository historyRepository;
    private final MinecraftServerRepository serverRepository;

    public ServerStatusHistoryService(
            ServerStatusHistoryRepository historyRepository,
            MinecraftServerRepository serverRepository
    ) {
        this.historyRepository = historyRepository;
        this.serverRepository = serverRepository;
    }

    public ServerStatusHistory save(ServerStatusHistory history) {
        return historyRepository.save(history);
    }

    public Page<ServerStatusHistory> findHistory(
            Long serverId,
            Pageable pageable
    ) {

        MinecraftServer server =
                serverRepository.findById(serverId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Servidor não encontrado"
                                )
                        );

        return historyRepository
                .findByServerOrderByCheckedAtDesc(
                        server,
                        pageable
                );
    }

    public List<ServerStatusHistory> findEntitiesByServerId(
            Long serverId,
            LocalDateTime startTime
    ) {

        serverRepository.findById(serverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Servidor não encontrado"
                        )
                );

        return historyRepository.findEntitiesByServerId(
                serverId,
                startTime
        );
    }
}