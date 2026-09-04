package com.lucas.minecraft_monitor.repository;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinecraftServerRepository
        extends JpaRepository<MinecraftServer, Long> {
}