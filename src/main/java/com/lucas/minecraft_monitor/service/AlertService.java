package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;

public interface AlertService {

    void serverDown(MinecraftServer server);

    void serverUp(MinecraftServer server);
}