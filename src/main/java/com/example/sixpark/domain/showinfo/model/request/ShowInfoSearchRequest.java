package com.example.sixpark.domain.showinfo.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowInfoSearchRequest {

    // 검색을 다양한 형태로 입력할수가 있어서, 유효성 검사를 하지 않음.
    // 5가지의 옵션중 1가지의 옵션으로 검색 가능

    private String prfnm;       // 공연 제목
    private String prfcast;     // 출연진
    private String area;        // 지역
    private String fcltynm;     // 시설 이름
    private String dtguidance;  // 공연 일정

    // 검색 조건이 하나라도 있는지 확인
    public boolean hasSearchCondition() {
        return isNotBlank(prfnm) ||
                isNotBlank(prfcast) ||
                isNotBlank(area) ||
                isNotBlank(fcltynm) ||
                isNotBlank(dtguidance);
    }

    // 문자열이 null이 아니고 비어있지 않은지 확인
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
