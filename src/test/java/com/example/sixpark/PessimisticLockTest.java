package com.example.sixpark;

import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.genre.repository.GenreRepository;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.seat.model.request.SeatSelectRequest;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import com.example.sixpark.domain.seat.service.SeatService;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showplace.repository.ShowPlaceRepository;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import com.example.sixpark.domain.showschedule.repository.ShowScheduleRepository;
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
    ShowPlaceRepository showPlaceRepository;
    @Autowired
    ShowScheduleRepository showScheduleRepository;
    @Autowired
    GenreRepository genreRepository;

    @Test
    void 동시에_좌석_선택_테스트_Redis_Lock() {
        // given
        setUpSeatData();

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger successCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                SeatSelectRequest request = new SeatSelectRequest();
                ReflectionTestUtils.setField(request, "scheduleId", 1L);
                ReflectionTestUtils.setField(request, "seatNo", 1);

                seatService.selectSeatRedisLock(request);
                successCount.incrementAndGet(); // 성공 카운트
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        // when & then
        run(executor, successCount, task);
    }

    @Test
    void 동시에_좌석_선택_테스트() {
        // given
        setUpSeatData();

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger successCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                SeatSelectRequest request = new SeatSelectRequest();
                ReflectionTestUtils.setField(request, "scheduleId", 1L);
                ReflectionTestUtils.setField(request, "seatNo", 1);

                seatService.selectSeatLOCK(request);
                successCount.incrementAndGet(); // 성공 카운트
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        // when & then
        run(executor, successCount, task);
    }

    @Test
    void 동시에_좌석_선택_테스트_락없음 () {
        // given
        setUpSeatData();

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger successCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                SeatSelectRequest request = new SeatSelectRequest();
                ReflectionTestUtils.setField(request, "scheduleId", 1L);
                ReflectionTestUtils.setField(request, "seatNo", 1);

                seatService.selectSeatNoLock(request);
                successCount.incrementAndGet(); // 성공 카운트
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        // when & then
        run(executor, successCount, task);
    }

    private void setUpSeatData() {
        Genre genre = genreRepository.save(new Genre("뮤지컬"));

        ShowInfo showInfo = showInfoRepository.save(
                ShowInfo.create(
                        genre,
                        "TEST_MT20ID",
                        "테스트 공연",
                        List.of("배우1").toString(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        "poster.jpg",
                        12
                )
        );

        ShowPlace showPlace = showPlaceRepository.save(
                ShowPlace.create(
                        showInfo,
                        "서울",
                        "테스트 공연장",
                        100L,
                        "금요일(18:00,20:30)",
                        "1시간 30분"
                )
        );

        ShowSchedule showSchedule = showScheduleRepository.save(
                new ShowSchedule(showInfo, showPlace, LocalDate.now(), LocalTime.now())
        );

        seatRepository.save(new Seat(showSchedule, 1));
    }

    private void run(ExecutorService executor, AtomicInteger successCount, Runnable task) {
        // when
        executor.submit(task);
        executor.submit(task);
        executor.submit(task);

        executor.shutdown();

        try {
            Thread.sleep(5000); // 스레드 종료 대기
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // then
        assertThat(successCount.get())
                .as("동시에 여러 요청이 성공하면 안 된다")
                .isEqualTo(1); // 성공 카운트가 1개여야 테스트 통과
    }
}
