package com.chess.domain.model.user;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera getters, setters, equals, hashCode e toString automaticamente
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotNull
    private Role role;

    private LocalDateTime createdAt;

}
