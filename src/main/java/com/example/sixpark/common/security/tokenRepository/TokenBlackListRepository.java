package com.example.sixpark.common.security.tokenRepository;

import com.example.sixpark.common.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {

    boolean existsByToken(String token);
}
