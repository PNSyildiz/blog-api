package com.app.blog;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    public CommentController(CommentRepository commentRepository, PostRepository postRepository){ this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/comments")
    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    @GetMapping("/post/{post_id}/comments")
    public Iterable<Post> getAllCommentsFromPost(@PathVariable Long post_id){
        return commentRepository.findByPostId(post_id);
    }

    @GetMapping("/comment/{comment_id}")
    public Comment getCommentById(@PathVariable Long comment_id){
        return commentRepository.findById(comment_id).orElseThrow();
    }

    @PostMapping("/post/{post_id}/comment")
    public Comment createComment(@RequestBody Comment comment, @PathVariable Long post_id){

        Comment myComment = new Comment();
        myComment.setDescription(comment.getDescription());
        myComment.setDateTime(comment.getDateTime());
        myComment.setLikes(comment.getLikes());

        Post post = postRepository.findById(post_id).orElse(null);

        if(post != null){
            comment.setPost(post);
            return commentRepository.save(comment);
        } else {
            return null;
        }
    }

    @PutMapping("/comment/{comment_id}")
    public Comment updateComment(@PathVariable Long comment_id, @RequestBody Comment comment){
        Comment existingComment = commentRepository.findById(comment_id).orElseThrow();
        existingComment.setDescription(comment.getDescription());
        existingComment.setDateTime(comment.getDateTime());
        existingComment.setLikes(comment.getLikes());

        return commentRepository.save(existingComment);
    }

    @DeleteMapping("/comment/{comment_id}")
    public void deleteComment(@PathVariable Long comment_id){
        commentRepository.deleteById(comment_id);
    }
}
