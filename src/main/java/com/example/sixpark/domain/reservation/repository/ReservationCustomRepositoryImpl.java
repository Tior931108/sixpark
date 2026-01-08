package com.example.sixpark.domain.reservation.repository;

import com.example.sixpark.domain.reservation.entity.QReservation;
import com.example.sixpark.domain.reservation.medel.response.ReservationGetInfoResponse;
import com.example.sixpark.domain.seat.entity.QSeat;
import com.example.sixpark.domain.showschedule.entiry.QShowSchedule;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReservationGetInfoResponse> findMyReservations(
            Long userId, Boolean isDeleted, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    ) {
        QReservation r = QReservation.reservation;
        QSeat s = QSeat.seat;
        QShowSchedule ss = QShowSchedule.showSchedule;

        List<ReservationGetInfoResponse> content = queryFactory
                .select(Projections.constructor(ReservationGetInfoResponse.class,
                        r.id,
                        r.user.id,
                        s.seatNo,
                        ss.id,
                        r.createdAt,
                        r.isDeleted
                ))
                .from(r)
                .join(r.seat, s)
                .join(s.showSchedule, ss)
                .where(
                        userIdEq(userId),
                        isDeletedEq(isDeleted),
                        createdAtGoe(startDate),
                        createdAtLoe(endDate)
                )
                .orderBy(r.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(r.count())
                .from(r)
                .where(
                        userIdEq(userId),
                        isDeletedEq(isDeleted),
                        createdAtGoe(startDate),
                        createdAtLoe(endDate)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
    // BooleanExpression 방식 적용
    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? QReservation.reservation.user.id.eq(userId) : null;
    }

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        return isDeleted != null ? QReservation.reservation.isDeleted.eq(isDeleted) : null;
    }

    private BooleanExpression createdAtGoe(LocalDateTime startDate) {
        return startDate != null ? QReservation.reservation.createdAt.goe(startDate) : null;
    }

    private BooleanExpression createdAtLoe(LocalDateTime endDate) {
        return endDate != null ? QReservation.reservation.createdAt.loe(endDate) : null;
    }

}