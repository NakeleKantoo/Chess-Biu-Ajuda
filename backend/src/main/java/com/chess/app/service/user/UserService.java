package com.chess.app.service.user;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chess.domain.exception.user.DuplicateUsernameException;
import com.chess.domain.exception.user.UserNotFoundException;
import com.chess.domain.model.user.Role;
import com.chess.domain.model.user.User;
import com.chess.infrastructure.persistence.entity.UserEntity;
import com.chess.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.chess.infrastructure.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPersistenceMapper userMapper;

    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }

        log.info("Criando novo usuário: {}", username);
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);

        UserEntity entityFunc = userMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entityFunc);

        log.info("Usuário {} criado com sucesso", username);

        return userMapper.toDomain(savedEntity);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    public String getUsernameById(UUID id) {
        return userRepository.findById(id)
                .map(UserEntity::getUsername)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserEntity getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

}
