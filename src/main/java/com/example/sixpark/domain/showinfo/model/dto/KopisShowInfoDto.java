package com.example.sixpark.domain.showinfo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * KOPIS 공연 목록 xml > DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisShowInfoDto {

    @JacksonXmlProperty(localName = "mt20id")
    private String mt20id; // KOPIS 공연 ID

    @JacksonXmlProperty(localName = "prfnm")
    private String prfnm; // 공연 제목

    @JacksonXmlProperty(localName = "prfpdfrom")
    private String prfpdfrom; // 시작 날짜

    @JacksonXmlProperty(localName = "prfpdto")
    private String prfpdto; // 종료 날짜

    @JacksonXmlProperty(localName = "fcltynm")
    private String fcltynm; // 시설명

    @JacksonXmlProperty(localName = "poster")
    private String poster; // 이미지 주소

    @JacksonXmlProperty(localName = "area")
    private String area; // 지역

    @JacksonXmlProperty(localName = "genrenm")
    private String genrenm; // 장르명

    @JacksonXmlProperty(localName = "prfstate")
    private String prfstate; // 공연 진행상황 ex) 공연예정, 공연중, 공연완료
}
