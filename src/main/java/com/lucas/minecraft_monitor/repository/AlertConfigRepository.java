package com.lucas.minecraft_monitor.repository;

import com.lucas.minecraft_monitor.model.AlertChannel;
import com.lucas.minecraft_monitor.model.AlertConfig;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertConfigRepository
        extends JpaRepository<AlertConfig, Long> {

    List<AlertConfig> findByServer(MinecraftServer server);

    Optional<AlertConfig> findByServerAndChannel(
            MinecraftServer server,
            AlertChannel channel
    );

    List<AlertConfig> findByServerAndEnabledTrue(
            MinecraftServer server
    );
}
