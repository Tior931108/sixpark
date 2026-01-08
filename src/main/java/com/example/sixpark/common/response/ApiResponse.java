package com.example.sixpark.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 (데이터 없음)
    public static <Void> ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // 성공 응답 (커스텀 메시지)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 실패 응답
    public static <Void> ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // 실패 응답 (데이터 포함)
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
