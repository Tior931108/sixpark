package com.example.sixpark.domain.showtime.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisShowDetailDto {

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

    @JsonProperty("prfcast")
    private List<String> prfcast;

    @JsonProperty("prfcrew")
    private String prfcrew;

    @JsonProperty("prfruntime")
    private String prfruntime;

    @JsonProperty("prfage")
    private String prfage;

    @JsonProperty("pcseguidance")
    private String pcseguidance;

    @JsonProperty("poster")
    private String poster;

    @JsonProperty("area")
    private String area;

    @JsonProperty("dtguidance")
    private String dtguidance;

    @JsonProperty("mt10id")
    private String mt10id;
}
