package com.example.sixpark.domain.user.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.model.dto.UserDto;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {

        // 이메일 / 닉네임 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorMessage.EXIST_EMAIL);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorMessage.EXIST_NAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 생성 (role은 USER 고정)
        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getName(),
                request.getNickname(),
                request.getBirth()
        );

        userRepository.save(user);

        // DTO 변환
        return UserSignupResponse.from(UserDto.from(user));
    }

}
