package com.example.sixpark.domain.user.model.dto;

import com.example.sixpark.common.enums.UserRole;
import com.example.sixpark.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getBirth(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}
