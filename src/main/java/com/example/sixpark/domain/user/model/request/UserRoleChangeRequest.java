package com.example.sixpark.domain.user.model.request;

import com.example.sixpark.common.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRoleChangeRequest {

    @NotBlank(message = "변경하고자하는 권한을 입력해주세요.")
    private UserRole role;
}
