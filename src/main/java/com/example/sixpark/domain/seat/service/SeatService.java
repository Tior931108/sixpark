package com.example.sixpark.domain.seat.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.seat.model.dto.SeatDto;
import com.example.sixpark.domain.seat.model.request.SeatCreateRequest;
import com.example.sixpark.domain.seat.model.request.SeatSelectRequest;
import com.example.sixpark.domain.seat.model.response.SeatSelectResponse;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import com.example.sixpark.domain.showschedule.repository.ShowScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowScheduleRepository showScheduleRepository;
    private final LockService lockService;

    /**
     * 좌석 생성
     * @param request 스케줄 ID 범위
     */
    @Transactional
    public void createSeat(SeatCreateRequest request) {
        // 공연 스케줄 한번에 조회
        List<ShowSchedule> schedules = showScheduleRepository.findAllByRange(request.getStartScheduleId(), request.getEndScheduleId());

        // 못 찾은 게 있을 경우 예외 발생
        if (schedules.size() != request.getEndScheduleId() - request.getStartScheduleId() + 1) {
            throw new CustomException(ErrorMessage.NOT_FOUND_SCHEDULE);
        }

        List<Long> scheduleIds = schedules.stream().map(ShowSchedule::getId).toList();
        List<Long> existScheduleIds = seatRepository.findExistScheduleIds(scheduleIds); // 이미 좌석을 생성한 스케줄 ID 리스트
        List<Seat> seats = new ArrayList<>();
        for (ShowSchedule schedule : schedules) {
            if (existScheduleIds.contains(schedule.getId())) { // 이미 좌석을 생성한 스케줄인지 확인
                continue;
            }

            Long count = schedule.getShowPlace().getSeatscale(); // 좌석 수
            for (int i=0; i<count; i++) { // 좌석 수 만큼 생성
                Seat seat = new Seat(schedule, i+1);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    /**
     * 좌석 선택, redis 락 구현
     */
    @Transactional
    public SeatSelectResponse selectSeatRedisLock(SeatSelectRequest request) {
        // 좌석 조회
        Seat seat = seatRepository.findSeat(request.getScheduleId(), request.getSeatNo())
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_SEAT));

        return lockService.executeWithLock("lock:seat:" + seat.getId(),
                () -> {
                    // 이미 선택된 좌석인지 확인
                    if (seat.isSelected()) {
                        throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);
                    }

                    seat.select(true);
                    return SeatSelectResponse.from(SeatDto.from(seat));
                });
    }

    /**
     * 좌석 선택, 비관적 락 구현
     */
    @Transactional
    public void selectSeatLOCK(SeatSelectRequest request) {
        // 좌석 조회
        Seat seat = seatRepository.findSeatForLOCK(request.getScheduleId(), request.getSeatNo()) // 🔒 락 획득
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_SEAT));

        log.info("{} -> 락 획득 완료", Thread.currentThread().getName());
        log.info("선택된 좌석인지 확인: {}", seat.isSelected());

        // 이미 선택된 좌석인지 확인
        if (seat.isSelected()) {
            throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);
        }

        seat.select(true); // 좌석 선택
    }

    /**
     * 좌석 선택, 락 없는 버전
     */
    @Transactional
    public void selectSeatNoLock(SeatSelectRequest request) {
        // 좌석 조회
        Seat seat = seatRepository.findSeat(request.getScheduleId(), request.getSeatNo())
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_SEAT));

        log.info("{} -> 락 획득 완료", Thread.currentThread().getName());
        log.info("선택된 좌석인지 확인: {}", seat.isSelected());

        // 이미 선택된 좌석인지 확인
        if (seat.isSelected()) {
            throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);
        }

        seat.select(true); // 좌석 선택
    }
}
