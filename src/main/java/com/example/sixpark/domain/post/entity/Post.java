package com.example.sixpark.domain.post.entity;

import com.example.sixpark.common.entity.BaseEntity;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showinfoes_id")
    private ShowInfo showinfo;

    @Column(nullable = false)
    @Size(min = 1, max = 30)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Post(User user, ShowInfo showinfo, String title, String content) {
        this.user = user;
        this.showinfo = showinfo;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content.trim();
        }
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}