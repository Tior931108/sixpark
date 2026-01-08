package com.example.sixpark.domain.showinfo.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShowInfoCustomRepository {

    // v2: QueryDSL 동적 쿼리 검색
    Page<ShowInfo> searchShowInfosV2(ShowInfoSearchRequest request, Pageable pageable);
}
