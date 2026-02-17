package com.chess.app.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.chess.api.dto.response.auth.LoginResponse;
import com.chess.domain.exception.user.InvalidLoginException;
import com.chess.infrastructure.security.jwt.JwtProvider;
import com.chess.infrastructure.security.service.UserDetailsImpl;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider tokenProvider;
    
    public LoginResponse login(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password));
    
            SecurityContextHolder.getContext().setAuthentication(authentication);
    
            // 3. Gera o Token e os detalhes de resposta
            String jwt = tokenProvider.generateToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    
            return new LoginResponse(jwt, userDetails.getUsername(), userDetails.getRole());
        } catch (AuthenticationException e) {
            throw new InvalidLoginException();
        }
    }

}
