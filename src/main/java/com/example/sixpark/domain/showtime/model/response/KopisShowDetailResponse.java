package com.example.sixpark.domain.showtime.model.response;

import com.example.sixpark.domain.showtime.model.dto.KopisShowDetailDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisShowDetailResponse {

    private Dbs dbs;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dbs {
        @JsonProperty("db")
        private KopisShowDetailDto performanceDetail;
    }
}
