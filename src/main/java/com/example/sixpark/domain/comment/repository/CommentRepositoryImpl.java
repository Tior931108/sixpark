package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.model.dto.CommentSearchQueryDto;
import com.example.sixpark.domain.comment.model.dto.QCommentSearchQueryDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.example.sixpark.domain.comment.entity.QComment.comment;
import static com.example.sixpark.domain.post.entity.QPost.post;
import static com.example.sixpark.domain.user.entity.QUser.user;


@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentSearchQueryDto> getComments(Long postId, String searchKey, Pageable pageable) {
        List<CommentSearchQueryDto> result = queryFactory
                .select(new QCommentSearchQueryDto(
                        comment.id,
                        post.id,
                        user.id,
                        user.nickname,
                        comment.content,
                        comment.parentComment.id,
                        comment.createdAt,
                        comment.modifiedAt
                        ))
                .from(comment)
                .leftJoin(comment.post, post)
                .leftJoin(comment.user, user)
                .where(
                        contentCondition(searchKey),
                        postIdCondition(postId)
                )
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = queryFactory
                .select(comment.id.countDistinct())
                .from(comment)
                .where(
                        contentCondition(searchKey),
                        postIdCondition(postId)
                )
                .fetchOne();

        if(total == null) {
            total = 0L;
        }
        return new PageImpl<>(result, pageable, total);
    }

    private BooleanExpression contentCondition(String searchKey) {
        return searchKey != null ? comment.content.contains(searchKey) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {

        if (sort.isUnsorted()) {
            return new OrderSpecifier[]{ comment.createdAt.desc() };
        }

        return sort.stream()
                .map(order -> {
                    if ("createdAt".equals(order.getProperty())) {
                        return order.isAscending()
                                ? comment.createdAt.asc()
                                : comment.createdAt.desc();
                    }
                    return comment.createdAt.desc();
                })
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression postIdCondition(Long postId) {
        return postId != null ? comment.post.id.eq(postId) : null;
    }

}
