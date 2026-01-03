package com.example.sixpark.domain.user.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.model.dto.UserDto;
import com.example.sixpark.domain.user.model.request.UserPasswordChangeRequest;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.request.UserUpdateRequest;
import com.example.sixpark.domain.user.model.response.UserGetResponse;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.model.response.UserUpdateResponse;
import com.example.sixpark.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 내 정보조회 API 비지니스 로직
     */
    @Transactional(readOnly = true)
    public UserGetResponse getMyInfo(Long userId) {
        User user = getUserByIdOrThrow(userId);

        return UserGetResponse.from(UserDto.from(user));
    }

    /**
     * 유저 정보 수정 비지니스 로직
     */
    @Transactional
    public UserUpdateResponse updateMyInfo(Long userId, UserUpdateRequest request) {

        User user = getUserByIdOrThrow(userId);

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorMessage.EXIST_NAME);
        }

        // 변경
        user.update(request.getName(), request.getNickname());

        return UserUpdateResponse.from(UserDto.from(user));
    }

    /**
     * 비밀번호 변경 비지니스 로직
     */
    @Transactional
    public void changePassword(Long userId, UserPasswordChangeRequest request) {

        User user = getUserByIdOrThrow(userId);

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorMessage.EXIST_AND_NEW_PASSWORD);
        }

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        user.changePassword(encodedNewPassword);
    }

    /**
     * 비밀번호 확인 API 비지니스 로직
     */
    @Transactional(readOnly = true)
    public void checkPassword(Long userId, String rawPassword) {

        User user = getUserByIdOrThrow(userId);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ErrorMessage.NOT_MATCH_PASSWORD);
        }
    }

    /**
     * 회원 탈퇴 API 비지니스 로직
     */
    @Transactional
    public void deleteUser(Long userId) {

        User user = getUserByIdOrThrow(userId);

        user.softDelete();
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
