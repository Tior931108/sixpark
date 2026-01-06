package com.example.sixpark.domain.seat.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.seat.model.dto.SeatDto;
import com.example.sixpark.domain.seat.model.request.CreateSeatRequest;
import com.example.sixpark.domain.seat.model.request.SelectSeatRequest;
import com.example.sixpark.domain.seat.model.response.SelectSeatResponse;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import com.example.sixpark.domain.showschedule.repository.ShowScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowScheduleRepository showScheduleRepository;

    /**
     * 좌석 생성
     * @param requestList 스케줄 ID 리스트
     */
    public void createSeat(List<CreateSeatRequest> requestList) {
        // 공연 스케줄 id를 Set으로 받아서 중복 제거
        Set<Long> scheduleIds = requestList.stream()
                .map(CreateSeatRequest::getScheduleId).collect(Collectors.toSet());
        // 공연 스케줄 한번에 조회
        List<ShowSchedule> schedules = showScheduleRepository.findAllById(scheduleIds);

        // 못 찾은 게 있을 경우 예외 발생
        if (scheduleIds.size() != schedules.size()) {
            throw new CustomException(ErrorMessage.NOT_FOUND_SCHEDULE);
        }

        List<Seat> seats = new ArrayList<>();
        for (ShowSchedule schedule : schedules) {
            // 좌석이 이미 존재하는지 확인
            if (seatRepository.existsByShowSchedule(schedule)) continue;

            Long count = schedule.getShowPlace().getSeatscale(); // 좌석 수
            for (int i=0; i<count; i++) { // 좌석 수 만큼 생성
                Seat seat = new Seat(schedule, i+1);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    /**
     * 좌석 선택
     * @param request 좌석 선택 요청 DTO (좌석 ID)
     * @return 좌석 선택 응답 DTO (좌석 ID)
     */
    public SelectSeatResponse selectSeat(SelectSeatRequest request) {
        // 좌석 조회
        Seat seat = seatRepository.findByIdForLOCK(request.getSeatId()) // 🔒 락 획득
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_SEAT));

        log.info("{} -> 락 획득 완료", Thread.currentThread().getName());
        log.info("선택된 좌석인지 확인: {}", seat.isSelected());

        // 이미 선택된 좌석인지 확인
        if (seat.isSelected()) throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);

        seat.select(true); // 좌석 선택

        return SelectSeatResponse.from(SeatDto.from(seat));
    }

    /**
     * 좌석 선택, 락 없는 버전
     */
    public SelectSeatResponse selectSeatNoLock(SelectSeatRequest request) {
        // 좌석 조회
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_SEAT));

        log.info("{} -> 락 획득 완료", Thread.currentThread().getName());
        log.info("선택된 좌석인지 확인: {}", seat.isSelected());

        // 이미 선택된 좌석인지 확인
        if (seat.isSelected()) throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);

        seat.select(true); // 좌석 선택

        return SelectSeatResponse.from(SeatDto.from(seat));
    }
}
