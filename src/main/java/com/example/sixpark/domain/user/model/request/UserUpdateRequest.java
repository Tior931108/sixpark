package com.example.sixpark.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "20자 이내로 변경하고자하는 이름을 입력해주세요.")
    @Size(min = 2, max = 20)
    private String name;

    @NotBlank(message = "변경하고자하는 별명을 20자 이내로 입력해주세요.")
    @Size(min = 2, max = 20)
    private String nickname;
}
