package com.app.blog;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {

    @Autowired
    private ResourceLoader resourceLoader; // Autowire ResourceLoader
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Resource loadImageAsResource(String imageName) throws IOException {
        Resource resource = (Resource) resourceLoader.getResource("file:your-image-directory/" + imageName);
        if (((org.springframework.core.io.Resource) resource).exists() || ((org.springframework.core.io.Resource) resource).isReadable()) {
            return resource;
        } else {
            throw new IOException("Could not read the image file: " + imageName);
        }
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    @GetMapping("/user/{id}/posts")
    public Iterable<Post> getAllPostsFromUser(@PathVariable Long id){
        return postRepository.findByUserId(id);
    }

    @GetMapping("/post/{post_id}")
    public Post getPostById(@PathVariable Long post_id){
        return postRepository.findById(post_id).orElseThrow();
    }

    @PostMapping("/user/{user_id}/post")
    public Post createPost(@RequestPart("imageUrl") MultipartFile file,
                           @RequestParam("title") String title,
                           @RequestParam("description") String description,
                           @RequestParam("dataTime") Date dataTime,
                           @RequestParam("views") Integer views,
                           @RequestParam("likes") Integer likes,
                           @PathVariable Long user_id) throws IOException {

        String uploadDirectory = System.getProperty("user.dir") + File.separator + "src/main/resources/static/";
        Path imagePath = Paths.get(uploadDirectory, file.getOriginalFilename());
        Files.write(imagePath, file.getBytes());


        Post myPost = new Post();
        myPost.setTitle(title);
        myPost.setDescription(description);
        myPost.setImageUrl(file.getOriginalFilename());
        myPost.setLikes(likes);
        myPost.setViews(views);
        myPost.setDataTime(dataTime);
        // Find the User entity by user_id (foreign key)
        User user = userRepository.findById(user_id).orElse(null);

        if (user != null) {
            myPost.setUser(user); // Set the User entity as the post's owner
            return postRepository.save(myPost); // Save the post
        } else {
            return null;
        }
    }

    @PutMapping("/post/{post_id}")
    public Post updatePost(@PathVariable Long post_id, @RequestBody Post post){
        Post existingPost = postRepository.findById(post_id).orElseThrow();
        existingPost.setTitle(post.getTitle());
        existingPost.setDescription(post.getDescription());

        return postRepository.save(existingPost);
    }

    @PutMapping("/view/{post_id}")
    public Post view(@PathVariable Long post_id, @RequestBody Post post){
        Post existingPost = postRepository.findById(post_id).orElseThrow();
        existingPost.setViews(post.getViews());

        return postRepository.save(existingPost);
    }

    @PutMapping("/like/{id}")
    public Post like(@PathVariable Long post_id, @RequestBody Post post){
        Post existingPost = postRepository.findById(post_id).orElseThrow();
        existingPost.setLikes(post.getLikes());
        return postRepository.save(existingPost);
    }

    @Transactional
    @DeleteMapping("/post/{post_id}")
    public void deletePost(@PathVariable Long post_id){
        commentRepository.deleteCommentsByPostId(post_id);
        Optional<Post> post = postRepository.findById(post_id);
        if (post.isPresent()) {
            postRepository.delete(post.get());
        }
    }


    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") long id) {
        String uploadDirectory = System.getProperty("user.dir") + File.separator + "src/main/resources/static/";

        Optional<Post> post = postRepository.findById(id);
        String imageName = post.get().getImageUrl();
        System.out.println(imageName);
        byte[] image = new byte[0];
        try {
            image = Files.readAllBytes(Paths.get(uploadDirectory+imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
