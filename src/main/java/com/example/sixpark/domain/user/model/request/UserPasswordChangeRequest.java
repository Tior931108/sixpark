package com.example.sixpark.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPasswordChangeRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "8자 이상으로 새 비밀번호를 입력해주세요.")
    @Size(min = 8)
    private String newPassword;
}
