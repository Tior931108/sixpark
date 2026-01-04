package com.example.sixpark.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PageResponse<T> {

    private final boolean success;
    private final String message;
    private final PageData<T> data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public PageResponse(boolean success, String message, PageData<T> data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 커스텀 메시지와 함께
    public static <T> PageResponse<T> success(String message, Page<T> page) {
        return new PageResponse<>(
                true,
                message,
                new PageData<>(page)
        );
    }

    // 실패 응답
    public static <Void> PageResponse<Void> error(String message) {
        return new PageResponse<>(false, message, null);
    }

    @Getter
    public static class PageData<T> {
        private final List<T> content;
        private final long totalElements;
        private final int totalPages;
        private final int size;
        private final int number;

        public PageData(Page<T> page) {
            this.content = page.getContent();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.size = page.getSize();
            this.number = page.getNumber();
        }
    }
}
