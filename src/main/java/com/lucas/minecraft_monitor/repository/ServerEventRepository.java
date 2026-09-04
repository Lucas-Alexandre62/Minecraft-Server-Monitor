package com.lucas.minecraft_monitor.repository;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServerEventRepository
        extends JpaRepository<ServerEvent, Long> {

    Optional<ServerEvent> findTopByServerOrderByCreatedAtDesc(
            MinecraftServer server
    );

    @Query(
        "SELECT e FROM ServerEvent e " +
        "WHERE e.server = :server " +
        "AND e.type IN (com.lucas.minecraft_monitor.model.ServerEvent$EventType.SERVER_UP, " +
        "com.lucas.minecraft_monitor.model.ServerEvent$EventType.SERVER_DOWN) " +
        "ORDER BY e.createdAt DESC"
    )
    Optional<ServerEvent> findTopStateEventByServer(
            @Param("server") MinecraftServer server
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