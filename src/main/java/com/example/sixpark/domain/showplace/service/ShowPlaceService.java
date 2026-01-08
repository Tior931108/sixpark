package com.example.sixpark.domain.showplace.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showplace.repository.ShowPlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowPlaceService {

    private final ShowPlaceRepository showPlaceRepository;

    /**
     * ShowInfo ID로 ShowPlace 조회 (예외 처리 포함)
     */
    public ShowPlace getShowPlaceById(Long showInfoId) {
        return showPlaceRepository.findByShowInfoId(showInfoId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_SHOWPLACE));
    }
}
