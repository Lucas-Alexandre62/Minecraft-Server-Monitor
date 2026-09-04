package com.lucas.minecraft_monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.ProviderManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.lucas.minecraft_monitor.exception.ApiAccessDeniedHandler;
import com.lucas.minecraft_monitor.exception.ApiAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder
    ) {

        var user = User
                .withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public SecretKey jwtSecretKey() {

        return new SecretKeySpec(
                jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey jwtSecretKey
    ) {

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey)
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey jwtSecretKey
    ) {

        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ApiAuthenticationEntryPoint authenticationEntryPoint,
            ApiAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/api/auth/login",
                                        "/error"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(
                                Customizer.withDefaults()
                        )
                );

        return http.build();
    }
}
