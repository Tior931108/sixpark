package com.example.sixpark.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sixpark.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
}
