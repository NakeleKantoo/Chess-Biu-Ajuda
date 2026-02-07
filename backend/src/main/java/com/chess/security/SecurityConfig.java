package com.chess.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desativa proteção CSRF (necessário para POST funcionar sem token)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Libera TUDO para qualquer um
            );
        return http.build();
    }
}
