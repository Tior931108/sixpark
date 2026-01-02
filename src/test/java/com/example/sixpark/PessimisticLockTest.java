package com.example.sixpark;

import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.genre.repository.GenreRepository;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.seat.model.request.SelectSeatRequest;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import com.example.sixpark.domain.seat.service.SeatService;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.showtime.entity.ShowTime;
import com.example.sixpark.domain.showtime.repository.ShowTimeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PessimisticLockTest {

    @Autowired
    SeatService seatService;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    ShowInfoRepository showInfoRepository;
    @Autowired
    ShowTimeRepository showTimeRepository;
    @Autowired
    GenreRepository genreRepository;

    @Test
    void 동시에_좌석_선택_테스트() {
        // given
        Genre genre = genreRepository.save(new Genre("뮤지컬"));

        ShowInfo showInfo = showInfoRepository.save(
                new ShowInfo(
                        genre,
                        "TEST_MT20ID",
                        "테스트 공연",
                        List.of("배우1"),
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        "poster.jpg",
                        12
                )
        );

        ShowTime showTime = showTimeRepository.save(
                new ShowTime(
                        showInfo,
                        "서울",
                        "테스트 공연장",
                        100L,
                        LocalTime.of(19, 0)
                )
        );

        Seat seat = new Seat(showInfo, showTime);
        ReflectionTestUtils.setField(seat, "id", 1L);
        seatRepository.save(seat);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger successCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                SelectSeatRequest request = new SelectSeatRequest();
                ReflectionTestUtils.setField(request, "id", 1L);
                ReflectionTestUtils.setField(request, "showtime", showTime);

                seatService.selectSeat(request);
                successCount.incrementAndGet(); // 성공 카운트
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        // when (동시에 실행)
        executor.submit(task);
        executor.submit(task);
        executor.submit(task);

        executor.shutdown();

        try {
            Thread.sleep(1500); // 스레드 종료 대기
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // then
        assertThat(successCount.get())
                .as("동시에 여러 요청이 성공하면 안 된다")
                .isEqualTo(1); // 성공 카운트가 1개여야 테스트 통과
    }

    @Test
    void 동시에_좌석_선택_테스트_락없음 () {
        // given
        Genre genre = genreRepository.save(new Genre("뮤지컬"));

        ShowInfo showInfo = showInfoRepository.save(
                new ShowInfo(
                        genre,
                        "MT20ID_TEST",
                        "테스트 공연",
                        List.of("배우1"),
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        "poster.jpg",
                        12
                )
        );

        ShowTime showTime = showTimeRepository.save(
                new ShowTime(
                        showInfo,
                        "서울",
                        "테스트 공연장",
                        100L,
                        LocalTime.of(19, 0)
                )
        );

        Seat seat = new Seat(showInfo, showTime);
        ReflectionTestUtils.setField(seat, "id", 1L);
        seatRepository.save(seat);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger successCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                SelectSeatRequest request = new SelectSeatRequest();
                ReflectionTestUtils.setField(request, "id", 1L);
                ReflectionTestUtils.setField(request, "showtime", showTime);

                seatService.selectSeatNoLock(request);
                successCount.incrementAndGet(); // 성공 카운트
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        // when (동시에 실행)
        executor.submit(task);
        executor.submit(task);
        executor.submit(task);

        executor.shutdown();

        try {
            Thread.sleep(1500); // 스레드 종료 대기
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // then
        assertThat(successCount.get())
                .as("동시에 여러 요청이 성공하면 안 된다")
                .isEqualTo(1); // 성공 카운트가 1개여야 테스트 통과
    }
}
