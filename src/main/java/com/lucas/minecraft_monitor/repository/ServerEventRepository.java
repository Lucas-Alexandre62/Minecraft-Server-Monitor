package com.lucas.minecraft_monitor.repository;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServerEventRepository
        extends JpaRepository<ServerEvent, Long> {

    Optional<ServerEvent> findTopByServerOrderByCreatedAtDesc(
            MinecraftServer server
    );

    List<ServerEvent> findByServerOrderByCreatedAtDesc(
            MinecraftServer server
    );

    List<ServerEvent> findByServerAndTypeOrderByCreatedAtDesc(
            MinecraftServer server,
            ServerEvent.EventType type
    );

    Page<ServerEvent> findByServerOrderByCreatedAtDesc(
            MinecraftServer server,
            Pageable pageable
    );

    Page<ServerEvent> findByServerAndTypeOrderByCreatedAtDesc(
            MinecraftServer server,
            ServerEvent.EventType type,
            Pageable pageable
    );
}