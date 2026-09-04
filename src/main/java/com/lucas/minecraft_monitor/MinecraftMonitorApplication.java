package com.lucas.minecraft_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MinecraftMonitorApplication {

	public static void main(String[] args) {

		SpringApplication.run(
				MinecraftMonitorApplication.class,
				args
		);
	}
}