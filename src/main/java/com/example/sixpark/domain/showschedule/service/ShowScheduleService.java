package com.example.sixpark.domain.showschedule.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showplace.repository.ShowPlaceRepository;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import com.example.sixpark.domain.showschedule.medel.request.ShowScheduleCreateRequest;
import com.example.sixpark.domain.showschedule.repository.ShowScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShowScheduleService {

    private final ShowScheduleRepository showScheduleRepository;
    private final ShowPlaceRepository showPlaceRepository;

    private static final Map<String, DayOfWeek> DAY_MAP = Map.of(
            "월요일", DayOfWeek.MONDAY,
            "화요일", DayOfWeek.TUESDAY,
            "수요일", DayOfWeek.WEDNESDAY,
            "목요일", DayOfWeek.THURSDAY,
            "금요일", DayOfWeek.FRIDAY,
            "토요일", DayOfWeek.SATURDAY,
            "일요일", DayOfWeek.SUNDAY
    );

    /**
     * 스케줄 생성
     * @param request 공연 장소 id 범위
     */
    @Transactional
    public void createSchedule(ShowScheduleCreateRequest request) {
        // 공연 장소 한번에 조회
        List<ShowPlace> places = showPlaceRepository.findAllByRange(request.getStartPlaceId(), request.getEndPlaceId());

        // 못 찾은 게 있을 경우 예외 발생
        if (places.size() != request.getEndPlaceId() - request.getStartPlaceId() + 1) {
            throw new CustomException(ErrorMessage.NOT_FOUND_SHOWPLACE);
        }

        List<ShowSchedule> schedules = new ArrayList<>();
        for (ShowPlace place : places) { // 공연 장소마다 스케줄 생성
            // 요일별 공연 시간
            String[] blocks = place.getDtguidance().split(", "); // 예시: [금요일(18:00,20:30), 토요일 ~ 일요일(17:00,19:30)]
            Map<DayOfWeek, List<LocalTime>> map = dayTimeMap(blocks); // 배열 -> Map<요일, 시간 리스트>

            // 공연 정보 - 날짜
            ShowInfo info = place.getShowInfo();
            List<LocalDate> dates = generateShowDates(info); // 공연 날짜 리스트

            // 공연하는 날짜마다 해당 요일의 공연시간을 구하여 스케줄에 저장
            for (LocalDate date : dates) {
                DayOfWeek day = date.getDayOfWeek(); // 요일

                if (!map.containsKey(day)) {
                    continue;
                }

                for (LocalTime time : map.get(day)) { // 해당 요일의 공연시간마다 스케줄 저장
                    if(showScheduleRepository.existsByShowInfoAndShowPlaceAndShowDateAndShowTime(info, place, date, time)) {
                        continue;
                    }
                    schedules.add(new ShowSchedule(info, place, date, time));
                }
            }
        }
        // 생성되는 스케줄 한번에 모아서 저장
        showScheduleRepository.saveAll(schedules);
    }

    /**
     * 요일별 공연 시간 맵 생성
     * - 요일별 공연 시간을 파싱하여 맵에 저장
     * @param blocks 요일별 공연 시간 배열
     * @return 요일별 공연 시간 맵
     */
    private Map<DayOfWeek, List<LocalTime>> dayTimeMap(String[] blocks) {
        Map<DayOfWeek, List<LocalTime>> map = new HashMap<>();

        for (String block : blocks) {
            String dayPart = block.substring(0, block.indexOf("(")); // 요일 부분
            String timePart = block.substring(block.indexOf("(")+1, block.indexOf(")")); // 시간 부분
            List<LocalTime> times = Arrays.stream(timePart.split(",")).map(LocalTime::parse).toList(); // 시간 리스트

            if (dayPart.contains("~")) { // 요일이 범위인 경우
                String[] range = dayPart.split("~");
                DayOfWeek start = DAY_MAP.get(range[0].trim());
                DayOfWeek end   = DAY_MAP.get(range[1].trim());
                List<DayOfWeek> days = dayList(start, end); // 사이 요일

                for (DayOfWeek day : days) {
                    map.put(day, times); // 저장
                }
            } else { // 요일이 하나인 경우
                if (DAY_MAP.containsKey(dayPart)) {
                    map.put(DAY_MAP.get(dayPart), times); // 저장
                }
            }
        }
        
        return map;
    }

    /**
     * 요일 범위를 요일 리스트로 생성
     * - '시작 요일'과 '종료 요일' 사이의 모든 요일을 구한다.
     * @param start 시작 요일
     * @param end 종료 요일
     * @return 요일 리스트
     */
    private List<DayOfWeek> dayList(DayOfWeek start, DayOfWeek end) {
        List<DayOfWeek> result = new ArrayList<>();

        DayOfWeek day = start; // 시작 요일
        do {
            result.add(day); // 처음은 무조건 저장
            day = day.plus(1); // 다음날 요일
        } while(day != end.plus(1)); // 종료 요일을 지나면 종료

        return result;
    }

    /**
     * 공연 날짜 리스트 생성
     * - 공연 정보에서 '시작 날짜'와 '종료 날짜' 사이의 모든 날짜를 구한다.
     * @param info 공연 정보
     * @return 공연 날짜 리스트
     */
    private List<LocalDate> generateShowDates(ShowInfo info) {
        LocalDate start = info.getPrfpdfrom();
        LocalDate end = info.getPrfpdto();

        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) { // current가 end보다 이후가 아닐 때까지
            dates.add(current);
            current = current.plusDays(1); // 다음 날로 이동
        }

        return dates;
    }

}
