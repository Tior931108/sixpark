package com.example.sixpark.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sixpark.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
