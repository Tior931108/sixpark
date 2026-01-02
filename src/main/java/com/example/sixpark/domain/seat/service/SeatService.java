package com.example.sixpark.domain.seat.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.seat.model.dto.SeatDto;
import com.example.sixpark.domain.seat.model.request.SelectSeatRequest;
import com.example.sixpark.domain.seat.model.response.SelectSeatResponse;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;

    /**
     * 좌석 선택
     * @param request 좌석 선택 요청 DTO (좌석, 공연시간)
     * @return 좌석 선택 응답 DTO (좌석, 공연시간, 공연정보)
     */
    public SelectSeatResponse selectSeat(SelectSeatRequest request) {
        // 좌석 조회
        Seat seat = seatRepository.findByIdForLOCK(request.getSeatId(), request.getShowtimeId()) // 🔒 락 획득
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
        Seat seat = seatRepository.findBySeatIdAndShowtimeId(request.getSeatId(), request.getShowtimeId())
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_SEAT));

        log.info("{} -> 락 획득 완료", Thread.currentThread().getName());
        log.info("선택된 좌석인지 확인: {}", seat.isSelected());

        // 이미 선택된 좌석인지 확인
        if (seat.isSelected()) throw new CustomException(ErrorMessage.SEAT_ALREADY_SELECTED);

        seat.select(true); // 좌석 선택

        return SelectSeatResponse.from(SeatDto.from(seat));
    }
}
