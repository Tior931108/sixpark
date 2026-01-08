package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoRankingResponse;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowInfoRankingService {

    private final ShowInfoRepository showInfoRepository;
    private final ViewCountService viewCountService;

    /**
     * 장르별 일간 TOP10 조회
     */
    @Transactional(readOnly = true)
    public List<ShowInfoRankingResponse> getDailyTop10(Long genreId) {
        // Redis에서 TOP10 조회 (조회수 포함)
        Set<ZSetOperations.TypedTuple<Object>> topSet = viewCountService.getDailyTopN(genreId, 10);

        if (topSet == null || topSet.isEmpty()) {
            log.info("일간 조회수 데이터 없음: genreId={}", genreId);
            return Collections.emptyList();
        }

        // 랭킹 응답 공통 메소드
        List<ShowInfoRankingResponse> responses = getShowInfoRankingResponses(topSet);

        log.info("일간 TOP10 조회 완료: genreId={}, count={}", genreId, responses.size());
        return responses;
    }

    /**
     * 장르별 주간 TOP10 조회
     */
    @Transactional(readOnly = true)
    public List<ShowInfoRankingResponse> getWeeklyTop10(Long genreId) {
        // Redis에서 TOP10 조회 (조회수 포함)
        Set<ZSetOperations.TypedTuple<Object>> topSet = viewCountService.getWeeklyTopN(genreId, 10);

        if (topSet == null || topSet.isEmpty()) {
            log.info("주간 조회수 데이터 없음: genreId={}", genreId);
            return Collections.emptyList();
        }

        // 랭킹 응답 공통 메소드
        List<ShowInfoRankingResponse> responses = getShowInfoRankingResponses(topSet);

        log.info("주간 TOP10 조회 완료: genreId={}, count={}", genreId, responses.size());
        return responses;
    }

    /**
     * redis > ShowInfo ID 추출 및 랭킹 응답 공통 메소드
     */
    private List<ShowInfoRankingResponse> getShowInfoRankingResponses(Set<ZSetOperations.TypedTuple<Object>> topSet) {
        // ShowInfo ID 추출
        List<Long> showInfoIds = topSet.stream()
                .map(tuple -> Long.parseLong(tuple.getValue().toString()))
                .collect(Collectors.toList());

        // DB에서 ShowInfo 조회
        List<ShowInfo> showInfos = showInfoRepository.findByIdInWithGenre(showInfoIds);

        // Map으로 변환 (빠른 조회를 위해)
        Map<Long, ShowInfo> showInfoMap = showInfos.stream()
                .collect(Collectors.toMap(ShowInfo::getId, si -> si));

        // Response 생성 (순위 유지)
        List<ShowInfoRankingResponse> responses = new ArrayList<>();
        int rank = 1;

        for (ZSetOperations.TypedTuple<Object> tuple : topSet) {
            Long showInfoId = Long.parseLong(tuple.getValue().toString());
            Long viewCount = tuple.getScore().longValue();

            ShowInfo showInfo = showInfoMap.get(showInfoId);
            if (showInfo != null) {
                responses.add(new ShowInfoRankingResponse(showInfo, viewCount, rank++));
            }
        }
        return responses;
    }
    

    /**
     * 테스트용 랜덤 조회수 생성
     */
    @Transactional
    public void generateRandomViewsForGenre(Long genreId) {
        // 해당 장르의 모든 ShowInfo ID 조회
        List<Long> showInfoIds = showInfoRepository.findIdsByGenreId(genreId);

        if (showInfoIds.isEmpty()) {
            log.warn("장르에 공연이 없습니다: genreId={}", genreId);
            return;
        }

        // 랜덤 조회수 생성
        viewCountService.generateRandomViews(genreId, showInfoIds);

        log.info("랜덤 조회수 생성 완료: genreId={}, showInfoCount={}", genreId, showInfoIds.size());
    }
}
