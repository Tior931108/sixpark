package com.example.sixpark.common.utils;

import com.example.sixpark.common.enums.UserRole;
import com.example.sixpark.domain.comment.repository.CommentRepository;
import com.example.sixpark.domain.genre.repository.GenreRepository;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.reservation.repository.ReservationRepository;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.showplace.repository.ShowPlaceRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Administrator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws NoSuchFieldException, IllegalAccessException {

        // 관리자 계정
        User admin = new User (
                "admin@test.com",
                passwordEncoder.encode("admin123"),
                "관리자",
                "admin123",
                LocalDate.of(1995, 1, 1)
        );
        admin.changeRole(UserRole.ADMIN);
        userRepository.save(admin);

    }
}