package com.lucas.minecraft_monitor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alert_configs")
public class AlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private MinecraftServer server;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertChannel channel;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 500)
    private String url;

    public AlertConfig() {
    }

    public AlertConfig(
            MinecraftServer server,
            AlertChannel channel,
            boolean enabled
    ) {
        this.server = server;
        this.channel = channel;
        this.enabled = enabled;
    }

    public AlertConfig(
            MinecraftServer server,
            AlertChannel channel,
            boolean enabled,
            String url
    ) {
        this.server = server;
        this.channel = channel;
        this.enabled = enabled;
        this.url = url;
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

    public AlertChannel getChannel() {
        return channel;
    }

    public void setChannel(AlertChannel channel) {
        this.channel = channel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
