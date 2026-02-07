package com.chess.entity.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Marca a classe como uma entidade JPA
@Table(name = "users") // Especifica o nome da tabela no banco de dados
@Data // Gera getters, setters, equals, hashCode e toString automaticamente
@NoArgsConstructor // Cria um construtor sem argumentos (necessário para JPA)
@AllArgsConstructor // Cria um construtor com todos os argumentos (útil para testes e criação de objetos)
public class User {

    @Id // Marca o campo como chave primária (primary key)
    @GeneratedValue(strategy = GenerationType.UUID) // Gera automaticamente um UUID para cada novo usuário
    private UUID id;

    @NotBlank // Valida que o campo não pode ser nulo ou vazio
    @Size(min = 3, max = 20) // Valida que o campo deve ter entre 3 e 20 caracteres
    @Column(unique = true, nullable = false) // Especifica que o campo deve ser único e não nulo no banco de dados
    private String username;

    @NotBlank // Valida que o campo não pode ser nulo ou vazio
    @Size(max = 120) // Valida que o campo deve ter no máximo 120 caracteres
    @Column(nullable = false) // Especifica que o campo não pode ser nulo no banco de dados
    private String password;

    @NotNull // Valida que o objeto não pode ser nulo (Use NotNull para Enums, NotBlank é só para Strings)
    @Enumerated(EnumType.STRING) // Especifica que o campo é um enum e deve ser armazenado como string no banco de dados
    @Column(nullable = false) // Especifica que o campo não pode ser nulo no banco de dados
    private Role role;

    @CreationTimestamp // Marca o campo para ser preenchido automaticamente com a data e hora de criação
    private LocalDateTime createdAt;

}
