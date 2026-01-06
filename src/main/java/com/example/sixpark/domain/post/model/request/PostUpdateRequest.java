package com.example.sixpark.domain.post.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 30)
    private String title;

    @NotBlank
    private String content;
}
