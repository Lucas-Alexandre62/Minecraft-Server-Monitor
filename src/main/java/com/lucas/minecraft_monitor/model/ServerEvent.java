package com.lucas.minecraft_monitor.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "server_events")
public class ServerEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private MinecraftServer server;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum EventType {
        SERVER_DOWN,
        SERVER_UP,
        HIGH_LATENCY
    }

    public ServerEvent() {
    }

    public ServerEvent(MinecraftServer server, EventType type) {
        this.server = server;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public EventType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}