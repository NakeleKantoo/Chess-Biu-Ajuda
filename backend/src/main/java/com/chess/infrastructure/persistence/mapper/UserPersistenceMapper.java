package com.chess.infrastructure.persistence.mapper;

import com.chess.domain.model.user.User;
import com.chess.infrastructure.persistence.entity.UserEntity;

import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
            entity.getId(),
            entity.getUsername(),
            entity.getPassword(),
            entity.getRole(),
            entity.getCreatedAt()
        );
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        return new UserEntity(
            domain.getId(),
            domain.getUsername(),
            domain.getPassword(),
            domain.getRole(),
            domain.getCreatedAt()
        );
    }
}
