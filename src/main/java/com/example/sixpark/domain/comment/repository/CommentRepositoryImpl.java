package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.entity.QComment;
import com.example.sixpark.domain.comment.model.dto.CommentChildGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.CommentParentGetQueryDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import java.util.List;
import static com.example.sixpark.domain.comment.entity.QComment.comment;
import static com.example.sixpark.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory queryFactory;

    QComment childComment = new QComment("childComment");

    @Override
    public Slice<CommentParentGetQueryDto> getSearchComments(Long postId, String searchKey, Pageable pageable) {
        List<CommentParentGetQueryDto> result = queryFactory.select(Projections.constructor(CommentParentGetQueryDto.class, comment.id, comment.post.id, user.id, user.nickname, comment.content, comment.childCommentCount.coalesce(0L), comment.createdAt, comment.modifiedAt))
                .from(comment)
                .leftJoin(comment.user, user)
                .where(postIdCondition(postId), contentCondition(searchKey, postId), comment.parentComment.id.isNull(), comment.isDeleted.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(result, pageable);
    }

    @Override
    public Slice<CommentParentGetQueryDto> getParentComment(Long postId, Pageable pageable) {
        List<CommentParentGetQueryDto> commentList = queryFactory.select(Projections.constructor(CommentParentGetQueryDto.class, comment.id, comment.post.id, user.id, user.nickname, comment.content, comment.childCommentCount.coalesce(0L), comment.createdAt, comment.modifiedAt))
                .from(comment)
                .leftJoin(comment.user, user)
                .where(postIdCondition(postId), comment.parentComment.id.isNull(), comment.isDeleted.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(commentList, pageable);
    }

    @Override
    public Slice<CommentChildGetQueryDto> getChildComment(Long parentCommentId, Long postId, Pageable pageable) {
        List<CommentChildGetQueryDto> commentList = queryFactory.select(Projections.constructor(CommentChildGetQueryDto.class, comment.id, comment.post.id, user.id, user.nickname, comment.content, comment.parentComment.id, comment.createdAt, comment.modifiedAt))
                .from(comment)
                .leftJoin(comment.user, user)
                .where(postIdCondition(postId), comment.parentComment.id.isNotNull(), comment.parentComment.id.eq(parentCommentId), comment.isDeleted.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return checkEndPage(commentList, pageable);
    }

    private <Object> Slice<Object> checkEndPage(List<Object> commentList, Pageable pageable) {
        boolean hasNext = false;
        if (commentList.size() > pageable.getPageSize()) {
            hasNext = true;
            commentList.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(commentList, pageable, hasNext);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        // PageableDefault로 createAt에 정렬만 받아온다.
        Sort.Order order = sort.getOrderFor("createdAt");

        //new OrderSpecifier[]{comment.createdAt.asc(), comment.id.asc()}이거는 복합인덱스를 사용하기 위해서..
        return order.isAscending() ? new OrderSpecifier[]{comment.createdAt.asc(), comment.id.asc()} : new OrderSpecifier[]{comment.createdAt.desc(), comment.id.desc()};
    }

    private BooleanExpression contentCondition(String searchKey, Long postId) {
        if (searchKey == null || searchKey.isBlank()) {
            return null;
        }

        return comment.content.contains(searchKey)
                .or(JPAExpressions
                        .selectOne()
                        .from(childComment)
                        .where(childComment.post.id.eq(postId), childComment.parentComment.id.eq(comment.id), childComment.isDeleted.isFalse(), childComment.content.contains(searchKey))
                        .exists()
                );
    }

    private BooleanExpression postIdCondition(Long postId) {
        return comment.post.id.eq(postId);
    }
}