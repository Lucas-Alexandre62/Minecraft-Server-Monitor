package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.ApplicationUser;
import com.lucas.minecraft_monitor.repository.ApplicationUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository userRepository;

    public ApplicationUserDetailsService(
            ApplicationUserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        ApplicationUser applicationUser =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuário não encontrado"
                                )
                        );

        return User.builder()
                .username(applicationUser.getUsername())
                .password(applicationUser.getPasswordHash())
                .roles(applicationUser.getRole().name())
                .disabled(!applicationUser.isEnabled())
                .build();
    }
}