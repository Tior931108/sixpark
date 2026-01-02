package com.example.sixpark.domain.showinfo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * KOPIS 공연 목록 xml > DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisShowInfoDto {

    @JsonProperty("mt20id")
    private String mt20id;

    @JsonProperty("prfnm")
    private String prfnm;

    @JsonProperty("prfpdfrom")
    private String prfpdfrom;

    @JsonProperty("prfpdto")
    private String prfpdto;

    @JsonProperty("fcltynm")
    private String fcltynm;

    @JsonProperty("poster")
    private String poster;

    @JsonProperty("area")
    private String area;

    @JsonProperty("genrenm")
    private String genrenm;

    @JsonProperty("prfstate")
    private String prfstate;
}
