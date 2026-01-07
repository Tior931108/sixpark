package com.example.sixpark.support;

import com.example.sixpark.domain.comment.entity.Comment;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.user.entity.User;

public class CommentFixture {
    private static final String DEFAULT_CONTENT = "테스트 댓글";

    public static Comment createParentComment() {
        User writer = UserFixture.createUser();
        Post post = PostFixture.createPost();

        return new Comment(
                DEFAULT_CONTENT,
                post,
                writer,
                null
        );
    }

    public static Comment createChildComment(Comment parentComment) {
        User writer = UserFixture.createUser();
        Post post = PostFixture.createPost();

        return new Comment(
                DEFAULT_CONTENT,
                post,
                writer,
                parentComment
        );
    }
}