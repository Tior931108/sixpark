package com.example.sixpark.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserPasswordCheckRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
