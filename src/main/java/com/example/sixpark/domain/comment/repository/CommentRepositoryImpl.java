package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.entity.QComment;
import com.example.sixpark.domain.comment.model.dto.CommentGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.QCommentGetQueryDto;
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

    @Override
    public Slice<CommentGetQueryDto> getSearchComments(Long postId, String searchKey, Pageable pageable) {
        List<CommentGetQueryDto> result = queryFactory
                .select(new QCommentGetQueryDto(
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
                        postIdCondition(postId),
                        comment.parentComment.id.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(result, pageable);
    }

    @Override
    public Slice<CommentGetQueryDto> getParentComment(Long postId, Pageable pageable) {
        List<CommentGetQueryDto> commentList = queryFactory
                .select(new QCommentGetQueryDto(
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
                        comment.parentComment.id.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(commentList, pageable);
    }

    @Override
    public Slice<CommentGetQueryDto> getChildComment(Long parentCommentId, Long postId, Pageable pageable) {
         List<CommentGetQueryDto> commentList = queryFactory
                .select(new QCommentGetQueryDto(
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

    private Slice<CommentGetQueryDto> checkEndPage(List<CommentGetQueryDto> commentList, Pageable pageable) {
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

        QComment childComment = new QComment("childComment");

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