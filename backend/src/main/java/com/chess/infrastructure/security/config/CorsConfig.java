package com.chess.infrastructure.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite as credenciais (cookies, headers de autenticação)
        config.setAllowCredentials(true);
        
        // Origem do Front-end Angular
        config.setAllowedOrigins(List.of("https://leonnaviegas.dev.br"));
        
        // Headers permitidos (Content-Type, Authorization, etc)
        config.setAllowedHeaders(Arrays.asList(
                "Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        
        // Métodos HTTP que a API suporta
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica essa configuração para todos os endpoints (auth, users, games)
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }

}
