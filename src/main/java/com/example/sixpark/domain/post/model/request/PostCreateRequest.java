package com.example.sixpark.domain.post.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostCreateRequest {

    @NotNull(message = "공연 정보 ID는 필수입니다.")
    private Long showInfoId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 1, max = 30, message = "제목은 1자 이상 30자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
