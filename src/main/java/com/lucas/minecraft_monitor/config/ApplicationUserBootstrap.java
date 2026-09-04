package com.lucas.minecraft_monitor.config;

import com.lucas.minecraft_monitor.model.ApplicationUser;
import com.lucas.minecraft_monitor.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUserBootstrap implements CommandLineRunner {

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.username:}")
    private String username;

    @Value("${app.bootstrap.password:}")
    private String password;

    public ApplicationUserBootstrap(
            ApplicationUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (username.isBlank() || password.isBlank()) {
            return;
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        String passwordHash = passwordEncoder.encode(password);

        ApplicationUser user =
                new ApplicationUser(
                        username,
                        passwordHash,
                        ApplicationUser.Role.ADMIN
                );

        userRepository.save(user);
    }
}