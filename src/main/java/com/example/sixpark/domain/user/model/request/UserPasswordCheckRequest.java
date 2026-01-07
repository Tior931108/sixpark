package com.example.sixpark.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserPasswordCheckRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
