package com.example.sixpark.domain.user.model.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 20)
    private String name;

    @Size(min = 2, max = 20)
    private String nickname;
}
