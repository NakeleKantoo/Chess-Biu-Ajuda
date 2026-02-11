package com.chess.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.chess.infrastructure.security.jwt.JwtAuthenticationFilter;

/**
 * Configuração Principal de Segurança.
 * Define quem pode acessar o que e como a proteção funciona.
 */
@Configuration
public class SecurityConfig {

    /**
     * Define o nosso Filtro JWT como um Bean para o Spring gerenciar.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * O "Guardinha" principal. Define a cadeia de filtros de segurança (Security Filter Chain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita CSRF (Cross-Site Request Forgery). Como usamos JWT (stateless), não precisamos disso.
                // Se fosse uma aplicação MVC clássica com sessão no servidor, precisaria manter ativado.
                .csrf(csrf -> csrf.disable())
                
                // 2. Define as regras de quem pode acessar qual URL
                .authorizeHttpRequests(auth -> auth
                        // APIs públicas (sem autenticação)
                        .requestMatchers("/h2-console/**").permitAll() // Banco de dados em memória
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Documentação da API

                        // APIs de Usuário
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // Cadastro de usuários
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN") // Listar usuários

                        // APIs de Autenticação
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // Login de usuários

                        // APIs de Jogo
                        .requestMatchers(HttpMethod.POST, "/api/games").hasAnyRole("ADMIN", "USER") // Criar jogo
                        .requestMatchers(HttpMethod.POST, "/api/games/join").hasAnyRole("ADMIN", "USER") // Entrar em jogo
                        .requestMatchers(HttpMethod.GET, "/api/games/{gameId}").hasAnyRole("ADMIN", "USER") // Ver detalhes do jogo
                        .requestMatchers(HttpMethod.POST, "/api/games/{gameId}/move").hasAnyRole("ADMIN", "USER") // Fazer movimento
                        .requestMatchers(HttpMethod.POST, "/api/games/{gameId}/{action}").hasAnyRole("ADMIN", "USER") // Ações especiais (desistir, pedir empate, etc)
                        .requestMatchers(HttpMethod.GET, "/api/games/saved/{gameId}").permitAll() // Ver jogo salvo (pode ser público, sem autenticação)

                        // Qualquer outra rota não listada acima EXIGE autenticação por padrão
                        .anyRequest().authenticated()
                )
                
                // 3. Configurações Especiais para o Console do H2 funcionar (ele usa iframes)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                
                // 4. Gerenciamento de Sessão: STATELESS
                // Diz para o Spring Security: "Não crie JSESSIONID, não guarde estado no servidor".
                // Toda requisição é independente e deve trazer o token.
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 5. Instala nosso Filtro JWT ANTES do filtro padrão de usuário/senha do Spring
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Expõe o AuthenticationManager padrão do Spring para podermos usar no LoginController.
     * É ele quem de fato faz a validação da senha.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define o algoritmo de Criptografia de Senha.
     * BCrypt é o padrão da indústria atualmente. Ele gera salt automático e é lento de propósito para evitar BruteForce.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
