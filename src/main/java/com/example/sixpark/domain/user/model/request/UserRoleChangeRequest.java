package com.example.sixpark.domain.user.model.request;

import com.example.sixpark.common.enums.UserRole;
import lombok.Getter;

@Getter
public class UserRoleChangeRequest {

    private UserRole role;
}
