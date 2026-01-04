package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.domain.showinfo.model.dto.KopisShowInfoDto;
import com.example.sixpark.domain.showinfo.model.response.KopisShowListResponse;
import com.example.sixpark.domain.showtime.model.dto.KopisShowDetailDto;
import com.example.sixpark.domain.showtime.model.response.KopisShowDetailResponse;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
     * KOPIS API 공연 목록 조회
     */
    public List<KopisShowInfoDto> fetchShowInfoList(String startDate, String endDate, Integer cpage, Integer rows) {
        try {
            // 공연 정보 받아옴 (기본 1페이지, 공연갯수 100개)
            String url = String.format("%s/pblprfr?service=%s&stdate=%s&eddate=%s&cpage=%d&rows=%d",
                    apiUrl, apiKey, startDate, endDate, cpage, rows);

            log.info("KOPIS API 호출: {}", url);

            // 공통 UTF-8 변환 메서드로 XML 응답
            String xmlResponse = fetchXmlFromApi(url);

            // 추가: XML 응답 로그 출력
            log.debug("KOPIS API 원본 응답:\n{}", xmlResponse);

            KopisShowListResponse response = xmlMapper.readValue(
                    xmlResponse, KopisShowListResponse.class);

            if (response.getDb() == null || response.getDb().isEmpty()) {
                log.warn("KOPIS API 응답에 공연 데이터가 없습니다.");
                log.warn("응답 객체: {}", response);
                return new ArrayList<>();
            }

            log.info("공연 목록 조회 완료: {}건", response.getDb().size());
            return response.getDb();

        } catch (Exception e) {
            log.error("공연 목록 조회 실패", e);
            throw new RuntimeException("KOPIS API 호출 실패", e);
        }
    }

    /**
     * KOPIS API 공연 상세 정보 조회
     */
    public KopisShowDetailDto fetchShowTimeDetail(String mt20id) {
        try {
            String url = String.format("%s/pblprfr/%s?service=%s",
                    apiUrl, mt20id, apiKey);

            log.info("KOPIS API 상세 조회: {} ({})", mt20id, url);

            // 공통 UTF-8 변환 메서드로 XML 응답
            String xmlResponse = fetchXmlFromApi(url);

            KopisShowDetailResponse response = xmlMapper.readValue(
                    xmlResponse, KopisShowDetailResponse.class);

            return response.getDb();

        } catch (Exception e) {
            log.error("공연 상세 정보 조회 실패: {}", mt20id, e);
            return null;
        }
    }

    /**
     * KOPIS API 호출 및 UTF-8 XML 응답 받기 (공통 메서드)
     *
     * @param url API 호출 URL
     * @return UTF-8로 인코딩된 XML 문자열
     */
    private String fetchXmlFromApi(String url) {
        // HttpHeaders 설정 (UTF-8 명시)
        HttpHeaders headers = createUtf8Headers();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // UTF-8로 응답 받기
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class
        );

        // byte[]를 UTF-8 String으로 변환
        return new String(response.getBody(), StandardCharsets.UTF_8);
    }

    /**
     * UTF-8 HttpHeaders 생성 (공통 메서드)
     *
     * @return UTF-8로 설정된 HttpHeaders
     */
    private HttpHeaders createUtf8Headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        return headers;
    }

}
