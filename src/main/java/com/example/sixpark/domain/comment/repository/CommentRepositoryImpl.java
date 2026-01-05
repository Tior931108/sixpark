package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.entity.QComment;
import com.example.sixpark.domain.comment.model.dto.CommentChildGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.CommentParentGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.QCommentChildGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.QCommentParentGetQueryDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import java.util.List;
import static com.example.sixpark.domain.comment.entity.QComment.comment;
import static com.example.sixpark.domain.post.entity.QPost.post;
import static com.example.sixpark.domain.user.entity.QUser.user;


@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory queryFactory;

    QComment childComment = new QComment("childComment");

    @Override
    public Slice<CommentParentGetQueryDto> getSearchComments(Long postId, String searchKey, Pageable pageable) {
        List<CommentParentGetQueryDto> result = queryFactory
                .select(new QCommentParentGetQueryDto(
                        comment.id,
                        post.id,
                        user.id,
                        user.nickname,
                        comment.content,
                        childComment.id.count().coalesce(0L),
                        comment.createdAt,
                        comment.modifiedAt
                        ))
                .from(comment)
                .leftJoin(comment.post, post)
                .leftJoin(comment.user, user)
                .leftJoin(childComment).on(childComment.parentComment.id.eq(comment.id))
                .where(
                        contentCondition(searchKey),
                        postIdCondition(postId),
                        comment.parentComment.id.isNull()
                )
                .groupBy(
                        comment.id,
                        post.id,
                        user.id,
                        user.nickname,
                        comment.content,
                        comment.createdAt,
                        comment.modifiedAt
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(result, pageable);
    }

    @Override
    public Slice<CommentParentGetQueryDto> getParentComment(Long postId, Pageable pageable) {
        List<CommentParentGetQueryDto> commentList = queryFactory
                .select(new QCommentParentGetQueryDto(
                        comment.id,
                        post.id,
                        user.id,
                        user.nickname,
                        comment.content,
                        childComment.id.count().coalesce(0L),
                        comment.createdAt,
                        comment.modifiedAt
                ))
                .from(comment)
                .leftJoin(comment.post, post)
                .leftJoin(comment.user, user)
                .leftJoin(childComment).on(childComment.parentComment.id.eq(comment.id))
                .where(
                        postIdCondition(postId),
                        comment.parentComment.id.isNull()
                )
                .groupBy(
                        comment.id,
                        post.id,
                        user.id,
                        user.nickname,
                        comment.content,
                        comment.createdAt,
                        comment.modifiedAt
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(commentList, pageable);
    }

    @Override
    public Slice<CommentChildGetQueryDto> getChildComment(Long parentCommentId, Long postId, Pageable pageable) {
         List<CommentChildGetQueryDto> commentList = queryFactory
                .select(new QCommentChildGetQueryDto(
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
                        postIdCondition(postId),
                        comment.parentComment.id.isNotNull(),
                        comment.parentComment.id.eq(parentCommentId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(commentList, pageable);
    }

    private <T> Slice<T> checkEndPage(List<T> commentList, Pageable pageable) {
        boolean hasNext = false;
        if(commentList.size()> pageable.getPageSize()){
            hasNext = true;
            commentList.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(commentList, pageable, hasNext);
    }

    private BooleanExpression contentCondition(String searchKey) {
        if (searchKey == null || searchKey.isBlank()) {
            return null;
        }

        return comment.content.contains(searchKey)
                .or(
                        JPAExpressions
                                .selectOne()
                                .from(childComment)
                                .where(
                                        childComment.parentComment.id.eq(comment.id),
                                        childComment.content.contains(searchKey)
                                        )
                                .exists()
                );
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