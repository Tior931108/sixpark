package com.example.sixpark.common.security.service;

import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security가 "이 이메일의 사용자가 누구냐?" 라고 물어볼 때 호출되는 클래스
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 로그인 or JWT 인증 시 자동 호출
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

                return new AuthUser(user.getId(),user.getEmail(),user.getPassword(),user.getRole());
    }
}
