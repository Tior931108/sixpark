package com.example.sixpark.domain.showinfo.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import static com.example.sixpark.domain.genre.entity.QGenre.genre;
import static com.example.sixpark.domain.showinfo.entity.QShowInfo.showInfo;
import static com.example.sixpark.domain.showplace.entity.QShowPlace.showPlace;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShowInfoRepositoryImpl implements ShowInfoCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ShowInfo> searchShowInfosV2(ShowInfoSearchRequest request, Pageable pageable) {

        // 동적 OR 조건 빌드
        BooleanBuilder searchConditions = buildSearchConditions(request);

        // 메인 쿼리 (Fetch Join으로 N+1 방지)
        List<ShowInfo> content = queryFactory
                .selectFrom(showInfo)
                .distinct()  // 중복 제거
                .leftJoin(showInfo.showPlace, showPlace).fetchJoin()
                .leftJoin(showInfo.genre, genre).fetchJoin()
                .where(
                        showInfo.isDeleted.isFalse(),
                        searchConditions  // 동적 검색 조건
                )
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리 최적화 (Fetch Join 없이)
        JPAQuery<Long> countQuery = queryFactory
                .select(showInfo.countDistinct())
                .from(showInfo)
                .leftJoin(showInfo.showPlace, showPlace)
                .where(
                        showInfo.isDeleted.isFalse(),
                        searchConditions
                );

        log.info("QueryDSL 검색 완료: {} 건", content.size());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 동적 검색 조건 빌드 (OR 연산)
     */
    private BooleanBuilder buildSearchConditions(ShowInfoSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        // 각 검색 조건을 OR로 연결
        addOrCondition(builder, containsPrfnm(request.getPrfnm()));
        addOrCondition(builder, containsPrfcast(request.getPrfcast()));
        addOrCondition(builder, containsArea(request.getArea()));
        addOrCondition(builder, containsFcltynm(request.getFcltynm()));
        addOrCondition(builder, containsDtguidance(request.getDtguidance()));

        return builder;
    }

    /**
     * OR 조건 추가 헬퍼
     */
    private void addOrCondition(BooleanBuilder builder, BooleanExpression condition) {
        if (condition != null) {
            builder.or(condition);
        }
    }

    /**
     * 동적 정렬 처리
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (sort.isEmpty()) {
            // 기본 정렬: ID 내림차순
            orders.add(showInfo.id.desc());
        } else {
            sort.forEach(order -> {
                switch (order.getProperty()) {
                    case "id":
                        orders.add(order.isAscending() ? showInfo.id.asc() : showInfo.id.desc());
                        break;
                    case "prfnm":
                        orders.add(order.isAscending() ? showInfo.prfnm.asc() : showInfo.prfnm.desc());
                        break;
                    case "viewCount":
                        orders.add(order.isAscending() ? showInfo.viewCount.asc() : showInfo.viewCount.desc());
                        break;
                    case "prfpdfrom":
                        orders.add(order.isAscending() ? showInfo.prfpdfrom.asc() : showInfo.prfpdfrom.desc());
                        break;
                    default:
                        orders.add(showInfo.id.desc());
                }
            });
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // 검색 조건 메서드들
    private BooleanExpression containsPrfnm(String prfnm) {
        return isNotBlank(prfnm) ? showInfo.prfnm.containsIgnoreCase(prfnm) : null;
    }

    private BooleanExpression containsPrfcast(String prfcast) {
        return isNotBlank(prfcast) ? showInfo.prfcast.containsIgnoreCase(prfcast) : null;
    }

    private BooleanExpression containsArea(String area) {
        return isNotBlank(area) ? showInfo.showPlace.area.containsIgnoreCase(area) : null;
    }

    private BooleanExpression containsFcltynm(String fcltynm) {
        return isNotBlank(fcltynm) ? showInfo.showPlace.fcltynm.containsIgnoreCase(fcltynm) : null;
    }

    private BooleanExpression containsDtguidance(String dtguidance) {
        return isNotBlank(dtguidance) ? showInfo.showPlace.dtguidance.containsIgnoreCase(dtguidance) : null;
    }

    /**
     * 문자열 유효성 검사
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
