package com.chess.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que intercepta TODAS as requisições HTTP para procurar por um token JWT.
 * Se achar um token válido, ele autentica o usuário no sistema.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider; // Classe que valida o token

    @Autowired
    private UserDetailsService userDetailsService; // Classe que busca o usuário no banco

    /**
     * O método principal do filtro. É executado a cada requisição.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Tenta pegar o token (JWT) do cabeçalho "Authorization"
            String jwt = getJwtFromRequest(request);

            // 2. Se o token existir (não for null/vazio) e for VÁLIDO (assinatura ok, não expirou)
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                
                // 3. Descobre quem é o dono desse token (extrai o username)
                String username = tokenProvider.getUsernameFromJWT(jwt);

                // 4. Busca os dados completos do usuário no banco (Roles, Senha, ID, etc)
                // Isso garante que o usuário ainda existe no sistema.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Cria o objeto de autenticação oficial do Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // Adiciona detalhes extras da requisição (IP, SessionID, etc)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. FINALMENTE: "Loga" o usuário na sessão atual deste request.
                // A partir daqui, @PreAuthorize e SecurityContextHolder.getContext().getAuthentication() vão funcionar.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Se der erro na autenticação, apenas logamos e deixamos o request seguir sem autenticação (anonimo)
            logger.error("Não foi possível definir a autenticação do usuário: ", ex.getMessage());
        }

        // Continua a cadeia de filtros (vai para o próximo filtro ou para o Controller)
        filterChain.doFilter(request, response);
    }

    /**
     * Remove o prefixo "Bearer " e retorna apenas a string do token.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Verifica se o header existe e começa com "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retorna tudo depois do "Bearer "
        }
        return null;
    }

}
