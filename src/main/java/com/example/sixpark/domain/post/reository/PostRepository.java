package com.example.sixpark.domain.post.reository;

import com.example.sixpark.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<Post> findAllByIsDeletedFalse(Pageable pageable);
}