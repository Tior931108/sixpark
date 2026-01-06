package com.example.sixpark.domain.showschedule.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.showschedule.medel.request.ShowScheduleCreateRequest;
import com.example.sixpark.domain.showschedule.service.ShowScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShowScheduleController {

    private final ShowScheduleService showScheduleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/show-schedule")
    public ResponseEntity<ApiResponse<Void>> createSchedule(
            @Valid @RequestBody List<ShowScheduleCreateRequest> request
    ) {
        showScheduleService.createSchedule(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("공연 스케줄이 생성되었습니다."));
    }
}
