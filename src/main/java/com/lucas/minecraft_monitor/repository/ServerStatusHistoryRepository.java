package com.lucas.minecraft_monitor.repository;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.model.ServerStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerStatusHistoryRepository
        extends JpaRepository<ServerStatusHistory, Long> {

    List<ServerStatusHistory> findByServerOrderByCheckedAtDesc(
            MinecraftServer server
    );

    Page<ServerStatusHistory> findByServerOrderByCheckedAtDesc(
            MinecraftServer server,
            Pageable pageable
    );

    @Query("""
            SELECT h
            FROM ServerStatusHistory h
            WHERE h.server.id = :serverId
              AND h.checkedAt >= :startTime
            ORDER BY h.checkedAt DESC
            """)
    List<ServerStatusHistory> findEntitiesByServerId(
            @Param("serverId") Long serverId,
            @Param("startTime") LocalDateTime startTime
    );

    @Query("""
            SELECT h
            FROM ServerStatusHistory h
            WHERE h.server.id = :serverId
              AND h.checkedAt >= :startTime
            ORDER BY h.checkedAt ASC
            """)
    List<ServerStatusHistory> findMetricsByServerId(
            @Param("serverId") Long serverId,
            @Param("startTime") LocalDateTime startTime
    );
}
