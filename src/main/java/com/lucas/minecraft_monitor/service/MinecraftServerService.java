package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.repository.MinecraftServerRepository;
import com.lucas.minecraft_monitor.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinecraftServerService {

    private final MinecraftServerRepository repository;
    private final AlertConfigService alertConfigService;

    public MinecraftServerService(
            MinecraftServerRepository repository,
            AlertConfigService alertConfigService
    ) {
        this.repository = repository;
        this.alertConfigService = alertConfigService;
    }

    // CREATE
    public MinecraftServer create(MinecraftServer server) {
        MinecraftServer saved = repository.save(server);
        alertConfigService.initializeDefaults(saved.getId());
        return saved;
    }

    // FIND ALL
    public List<MinecraftServer> findAll() {
        return repository.findAll();
    }

    // FIND BY ID
    public MinecraftServer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Servidor não encontrado"));
    }

    // UPDATE
    public MinecraftServer update(
            Long id,
            MinecraftServer updatedServer
    ) {

        MinecraftServer server =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Servidor não encontrado"
                                )
                        );

        server.setName(updatedServer.getName());
        server.setHost(updatedServer.getHost());
        server.setPort(updatedServer.getPort());

        return repository.save(server);
    }

    // DELETE
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Servidor não encontrado");
        }

        repository.deleteById(id);
    }
}