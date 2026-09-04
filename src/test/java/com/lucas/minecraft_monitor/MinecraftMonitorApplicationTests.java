package com.lucas.minecraft_monitor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

@SpringBootTest
class MinecraftMonitorApplicationTests {

	private static final String TEST_USERNAME = "context-test-" + UUID.randomUUID();
	private static final String TEST_PASSWORD = UUID.randomUUID().toString();
	private static final String TEST_JWT_SECRET = UUID.randomUUID() + "-" + UUID.randomUUID();

	@DynamicPropertySource
	static void securityProperties(DynamicPropertyRegistry registry) {
		registry.add("app.bootstrap.username", () -> TEST_USERNAME);
		registry.add("app.bootstrap.password", () -> TEST_PASSWORD);
		registry.add("jwt.secret", () -> TEST_JWT_SECRET);
	}

	@Test
	void contextLoads() {
	}

}
