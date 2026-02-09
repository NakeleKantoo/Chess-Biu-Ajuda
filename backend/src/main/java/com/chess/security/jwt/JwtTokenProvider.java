package com.chess.security.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.chess.security.service.UserDetailsImpl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final long jwtExpirationInMs;
    private final SecretKey key;

    // Construtor: Prepara a "caneta especial" (SecretKey) que vamos usar para assinar
    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
                            @Value("${jwt.expiration}") long jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
        // Transforma sua string base64 em uma chave criptográfica real
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. GERAÇÃO: Cria o "Crachá" (Token) para o usuário levar pra casa
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Pega as roles (ex: "ROLE_USER")
        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // Quem é o dono?
                .claim("roles", roles)          // Informações extras
                .issuedAt(now)                        // Data de emissão
                .expiration(expiryDate)               // Data de validade
                .signWith(this.key)                   // Assina com a chave secreta
                .compact();                           // Empacota tudo numa String URL-safe
    }

    // 2. EXTRAÇÃO: Lê o crachá para saber quem está tentando entrar
    public String getUsernameFromJWT(String token) {
        return Jwts.parser()
                .verifyWith(this.key)       // Configura a chave para validar a assinatura
                .build()                    // Cria o leitor
                .parseSignedClaims(token)   // Abre o token (se a assinatura for válida)
                .getPayload()               // Pega o recheio (Payload)
                .getSubject();              // Pega o "subject" (que definimos como username)
    }

    // 3. VALIDAÇÃO: Verifica se o crachá é verdadeiro e não expirou
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(this.key)
                .build()
                .parseSignedClaims(authToken); // Se isso rodar sem erro, o token é válido!
            return true;
        } catch (JwtException ex) {
            // Se o token for falso, expirado, ou adulterado, a lib lança exceção JwtException (pai de todas as exceções JWT)
            System.err.println("Token inválido: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("Token está vazio ou nulo.");
        }
        return false;
    }
}
