package com.example.sixpark.domain.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class UserSignupRequest {

    @Email
    @NotBlank(message = "사용할 이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "사용할 비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "사용할 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "사용할 별명을 입력해주세요.")
    private String nickname;

    @NotNull(message = "생년/월/일을 입력해 주세요.")
    private LocalDate birth;
}
