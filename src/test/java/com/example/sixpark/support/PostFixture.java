package com.example.sixpark.support;

import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.user.entity.User;

public class PostFixture {

    private static final String DEFAULT_TITLE = "test title";
    private static final String DEFAULT_CONTENT = "test content";

    public static Post createPost() {
        User writer = UserFixture.createUser();
        ShowInfo showInfo = ShowInfoFixture.createShowInfo();

        return new Post(
                writer,
                showInfo,
                DEFAULT_TITLE,
                DEFAULT_CONTENT
        );
    }
}