package com.example.sixpark.domain.user.repository;

import com.example.sixpark.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByEmail(@Email @NotBlank String email);

    boolean existsByNickname(@NotBlank String nickname);
}
