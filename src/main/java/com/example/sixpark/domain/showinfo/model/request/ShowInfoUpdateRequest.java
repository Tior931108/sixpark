package com.example.sixpark.domain.showinfo.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShowInfoUpdateRequest {

    private String prfnm;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD 입니다.")
    private String prfpdfrom;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD 입니다.")
    private String prfpdto;

    @Positive(message = "좌석 가격은 0보다 커야 합니다.")
    private String pcseguidance;
}
