package com.lucas.minecraft_monitor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "minecraft_servers")
public class MinecraftServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do servidor é obrigatório")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "O host do servidor é obrigatório")
    @Column(nullable = false)
    private String host;

    @NotNull(message = "A porta do servidor é obrigatória")
    @Min(value = 1, message = "A porta deve ser maior ou igual a 1")
    @Max(value = 65535, message = "A porta deve ser menor ou igual a 65535")
    @Column(nullable = false)
    private Integer port;

    public MinecraftServer() {
    }

    public MinecraftServer(String name, String host, Integer port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}