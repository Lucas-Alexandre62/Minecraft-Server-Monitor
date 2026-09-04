package com.lucas.minecraft_monitor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MinecraftServerCreateRequest(
        @NotBlank(message = "O nome do servidor é obrigatório")
        String name,
        @NotBlank(message = "O host do servidor é obrigatório")
        String host,
        @NotNull(message = "A porta do servidor é obrigatória")
        @Min(value = 1, message = "A porta deve ser maior ou igual a 1")
        @Max(value = 65535, message = "A porta deve ser menor ou igual a 65535")
        Integer port
) {
}
