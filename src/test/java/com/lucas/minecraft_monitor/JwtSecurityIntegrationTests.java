package com.lucas.minecraft_monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.lucas.minecraft_monitor.repository.ApplicationUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "jwt.expiration=1"
})
@AutoConfigureMockMvc
class JwtSecurityIntegrationTests {

    private static final String TEST_USERNAME = "jwt-test-" + UUID.randomUUID();
    private static final String TEST_PASSWORD = UUID.randomUUID().toString();
    private static final String TEST_JWT_SECRET = UUID.randomUUID() + "-" + UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void securityProperties(DynamicPropertyRegistry registry) {
        registry.add("app.bootstrap.username", () -> TEST_USERNAME);
        registry.add("app.bootstrap.password", () -> TEST_PASSWORD);
        registry.add("jwt.secret", () -> TEST_JWT_SECRET);
    }

    @Test
    void loginIssuesBearerTokenAndAllowsProtectedRequest() throws Exception {
        String token = login();

        mockMvc.perform(get("/api/servers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedRequestWithoutBearerTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/servers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        String token = login();

        Thread.sleep(2_500);

        mockMvc.perform(get("/api/servers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsPreflightAllowsViteFrontend() throws Exception {
        mockMvc.perform(options("/api/servers")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "http://localhost:5173"
                ));
    }

    @Test
    void bootstrapUserIsPersistedWithPasswordHash() {
        var user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();

        org.junit.jupiter.api.Assertions.assertNotEquals(
                TEST_PASSWORD,
                user.getPasswordHash()
        );
        org.junit.jupiter.api.Assertions.assertTrue(
                passwordEncoder.matches(TEST_PASSWORD, user.getPasswordHash())
        );
    }

    private String login() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + TEST_USERNAME
                                + "\",\"password\":\"" + TEST_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(1))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return response.replaceFirst(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
    }
}
