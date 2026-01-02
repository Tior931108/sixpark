package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.domain.showinfo.model.dto.KopisShowInfoDto;
import com.example.sixpark.domain.showinfo.model.response.KopisShowListResponse;
import com.example.sixpark.domain.showtime.model.dto.KopisShowDetailDto;
import com.example.sixpark.domain.showtime.model.response.KopisShowDetailResponse;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KopisApiService {

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    @Value("${kopis.api.key}")
    private String apiKey;

    @Value("${kopis.api.url}")
    private String apiUrl;

    /**
     * 공연 목록 조회
     */
    public List<KopisShowInfoDto> fetchShowInfoList(String startDate, String endDate) {
        try {
            // 공연 정보 50개를 받아옴
            String url = String.format("%s/pblprfr?service=%s&stdate=%s&eddate=%s&rows=50",
                    apiUrl, apiKey, startDate, endDate);

            log.info("KOPIS API 호출: {}", url);

            String xmlResponse = restTemplate.getForObject(url, String.class);

            KopisShowListResponse response = xmlMapper.readValue(
                    xmlResponse, KopisShowListResponse.class);

            if (response.getDbs() == null || response.getDbs().getPerformances() == null) {
                log.warn("KOPIS API 응답에 공연 데이터가 없습니다.");
                return new ArrayList<>();
            }

            log.info("공연 목록 조회 완료: {}건", response.getDbs().getPerformances().size());
            return response.getDbs().getPerformances();

        } catch (Exception e) {
            log.error("공연 목록 조회 실패", e);
            throw new RuntimeException("KOPIS API 호출 실패", e);
        }
    }

    /**
     * 공연 상세 정보 조회
     */
    public KopisShowDetailDto fetchShowTimeDetail(String mt20id) {
        try {
            String url = String.format("%s/pblprfr/%s?service=%s",
                    apiUrl, mt20id, apiKey);

            log.info("KOPIS API 상세 조회: {} ({})", mt20id, url);

            String xmlResponse = restTemplate.getForObject(url, String.class);

            KopisShowDetailResponse response = xmlMapper.readValue(
                    xmlResponse, KopisShowDetailResponse.class);

            return response.getDbs().getPerformanceDetail();

        } catch (Exception e) {
            log.error("공연 상세 정보 조회 실패: {}", mt20id, e);
            return null;
        }
    }

}
