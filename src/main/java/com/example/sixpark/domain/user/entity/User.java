package com.example.sixpark.domain.user.entity;

import com.example.sixpark.common.entity.BaseEntity;
import com.example.sixpark.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(length = 100)
    private String name;

    @Column(unique = true)
    private String nickname;

    private LocalDate birth;

    @Column(length = 50)
    private UserRole role;

    private Boolean isDeleted = false;

    public User(String email, String password, String name, String nickname, LocalDate birth, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.role = role;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
