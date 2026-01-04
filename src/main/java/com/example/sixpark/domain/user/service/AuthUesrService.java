package com.example.sixpark.domain.user.service;

import com.example.sixpark.common.entity.TokenBlackList;
import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.enums.UserRole;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.common.security.jwt.JwtProvider;
import com.example.sixpark.common.security.tokenRepository.TokenBlackListRepository;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.model.dto.UserDto;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUesrService {

    private final JwtProvider jwtProvider;
    private final TokenBlackListRepository tokenBlacklistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 API 비지니스 로직
     */
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


    /**
     * 로그아웃 API 비지니스 로직
     */
    @Transactional
    public void logout(String token) {
        if (tokenBlacklistRepository.existsByToken(token)) {
            return;
        }

        tokenBlacklistRepository.save(
                new TokenBlackList(
                        token,
                        jwtProvider.getExpiration(token)
                )
        );
    }

    /**
     * 관리자 유저 전체 조회 API 비지니스 로직
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository
                .findAllByIsDeletedFalse(pageable)
                .map(UserDto::from);
    }


    /**
     * ADMIN 권한 권한 변경 API 비지니스 로직
     */
    @Transactional
    public void changeUserRole(Long userId, UserRole newRole) {
        User user = getUserByIdOrThrow(userId);
        user.changeRole(newRole);
    }


    /**
     * ADMIN 권한 여부 메서드
     */
    private void validateAdmin(AuthUser user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorMessage.ONLY_OWNER_ACCESS);
        }
    }

    /**
     * 공통 사용자 조회 메서드
     */
    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() ->
                        new CustomException(ErrorMessage.NOT_FOUND_USER)
                );
    }
}
