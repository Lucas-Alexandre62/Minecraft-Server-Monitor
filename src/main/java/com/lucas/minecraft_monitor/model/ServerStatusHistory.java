package com.lucas.minecraft_monitor.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "server_status_history")
public class ServerStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "server_id", nullable = false)
    private MinecraftServer server;

    @Column(nullable = false)
    private boolean online;

    @Column(nullable = false)
    private Integer playersOnline;

    @Column(nullable = false)
    private Integer maxPlayers;

    private Long latency;

    private String version;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

    public ServerStatusHistory() {
    }

    public ServerStatusHistory(
            MinecraftServer server,
            boolean online,
            Integer playersOnline,
            Integer maxPlayers,
            Long latency,
            String version,
            LocalDateTime checkedAt
    ) {
        this.server = server;
        this.online = online;
        this.playersOnline = playersOnline;
        this.maxPlayers = maxPlayers;
        this.latency = latency;
        this.version = version;
        this.checkedAt = checkedAt;
    }

    public Long getId() {
        return id;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Integer getPlayersOnline() {
        return playersOnline;
    }

    public void setPlayersOnline(Integer playersOnline) {
        this.playersOnline = playersOnline;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Long getLatency() {
        return latency;
    }

    public void setLatency(Long latency) {
        this.latency = latency;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}