package com.lucas.minecraft_monitor.exception;

public class MinecraftServerConnectionException extends RuntimeException {

    public MinecraftServerConnectionException(String message) {
        super(message);
    }

    public MinecraftServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}