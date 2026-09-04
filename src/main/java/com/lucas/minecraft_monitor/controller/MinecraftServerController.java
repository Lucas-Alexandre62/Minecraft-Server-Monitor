package com.lucas.minecraft_monitor.controller;

import com.lucas.minecraft_monitor.dto.MinecraftServerStatusDTO;
import com.lucas.minecraft_monitor.model.MinecraftServer;
import com.lucas.minecraft_monitor.service.MinecraftServerMonitorService;
import com.lucas.minecraft_monitor.service.MinecraftServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class MinecraftServerController {

    private final MinecraftServerService service;
    private final MinecraftServerMonitorService monitorService;

    public MinecraftServerController(
            MinecraftServerService service,
            MinecraftServerMonitorService monitorService
    ) {
        this.service = service;
        this.monitorService = monitorService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<MinecraftServer> create(
            @Valid @RequestBody MinecraftServer server
    ) {
        return ResponseEntity.ok(
                service.create(server)
        );
    }

    // FIND ALL
    @GetMapping
    public ResponseEntity<List<MinecraftServer>> findAll() {
        return ResponseEntity.ok(
                service.findAll()
        );
    }

    // FIND BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MinecraftServer> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.findById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MinecraftServer> update(
            @PathVariable Long id,
            @Valid @RequestBody MinecraftServer server
    ) {
        return ResponseEntity.ok(
                service.update(id, server)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    // CHECK SERVER STATUS
    @GetMapping("/{id}/status")
    public ResponseEntity<MinecraftServerStatusDTO> checkStatus(
            @PathVariable Long id
    ) {

        MinecraftServer server =
                service.findById(id);

        MinecraftServerStatusDTO status =
                monitorService.checkServer(
                        server.getHost(),
                        server.getPort()
                );

        return ResponseEntity.ok(status);
    }
}