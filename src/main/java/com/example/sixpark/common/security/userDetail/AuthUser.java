package com.example.sixpark.common.security.userDetail;

import com.example.sixpark.common.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security가 인증된 사용자를 표현할 때 사용하는 객체
 */
@Getter
public class AuthUser implements UserDetails {

    private final Long userId;     // 우리가 컨트롤러에서 뽑고 싶은 값
    private final String email;
    private final String password;
    private final UserRole role;

    public AuthUser(Long userId, String email, String password, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * 사용자의 권한 목록 반환
     * → ROLE_USER / ROLE_ADMIN
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole()));
    }

    /**
     * username 역할
     * email을 로그인 아이디로 사용
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override public String getPassword() { return password; }

    // 아래 4개는 보통 true 고정 (계정 잠금/만료 안 쓰는 경우)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
