package com.example.sixpark.domain.showtime.model.response;

import com.example.sixpark.domain.showtime.model.dto.KopisShowDetailDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "dbs")
public class KopisShowDetailResponse {

    @JacksonXmlProperty(localName = "db")
    private KopisShowDetailDto db;
}
