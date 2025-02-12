package com.example.project.controller;

import com.example.project.dto.PostDto;
import com.example.project.entity.Post;
import com.example.project.entity.requests.PostRequest;
import com.example.project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. Перегляд постів користувача (неавторизований доступ)
    @GetMapping("/{email}")
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable String email) {
        List<PostDto> posts = postService.getPostsByUserEmail(email);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    // 2. Створення посту для користувача (може тільки власник)
    // /api/users/posts?email=test@gmail.com
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestHeader("Authorization") String token, // Токен авторизації
            @RequestBody PostRequest postRequest) {  // Запит з даними для створення поста
        String email = postRequest.getEmail();
        String text = postRequest.getText();
        Post createdPost = postService.createPost(token, email, text);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
    // 3. Видалення посту для користувача (може тільки власник)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @RequestParam String email) {
        postService.deletePost(postId, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // 4. Редагування посту для користувача (може тільки власник)
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId, @RequestParam String email, @RequestBody String newText) {
        PostDto updatedPost = postService.updatePost(postId, email, newText);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }
}
