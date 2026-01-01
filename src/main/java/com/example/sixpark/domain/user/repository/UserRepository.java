package com.example.sixpark.domain.user.repository;

import com.example.sixpark.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
