package com.example.sixpark.domain.showtime.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * KOPIS 공연 상세 목록 xml > DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisShowDetailDto {

    @JacksonXmlProperty(localName = "mt20id")
    private String mt20id; // KOPIS 공연 고유 ID

    @JacksonXmlProperty(localName = "prfnm")
    private String prfnm; // 공연 제목

    @JacksonXmlProperty(localName = "prfpdfrom")
    private String prfpdfrom; // 시작 날짜

    @JacksonXmlProperty(localName = "prfpdto")
    private String prfpdto; // 종료 날짜

    @JacksonXmlProperty(localName = "fcltynm")
    private String fcltynm; // 시설명

    @JacksonXmlProperty(localName = "prfcast")
    private String prfcast; // 출연진

    @JacksonXmlProperty(localName = "prfcrew")
    private String prfcrew; // 연출진

    @JacksonXmlProperty(localName = "prfruntime")
    private String prfruntime; // 공연 총시간

    @JacksonXmlProperty(localName = "prfage")
    private String prfage; // 관람 나이(등급) ex) 만 15세 이용가, 전체 이용가

    @JacksonXmlProperty(localName = "pcseguidance")
    private String pcseguidance; // 좌석 가격

    @JacksonXmlProperty(localName = "poster")
    private String poster; // 이미지 주소

    @JacksonXmlProperty(localName = "area")
    private String area; // 지역

    @JacksonXmlProperty(localName = "dtguidance")
    private String dtguidance; // 일정 ex) 금요일(18:00,20:30), 토요일 ~ 일요일(17:00,19:30)

    @JacksonXmlProperty(localName = "mt10id")
    private String mt10id; // KOPIS 시설 ID
}
