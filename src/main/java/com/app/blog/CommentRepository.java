package com.app.blog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long post_id);

    void deleteCommentsByPostId(Long postId);
}
