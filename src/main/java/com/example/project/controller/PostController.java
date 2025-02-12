package com.example.project.controller;

import com.example.project.dto.PostDto;
import com.example.project.entity.Post;
import com.example.project.entity.requests.PostRequest;
import com.example.project.service.JwtService;
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
    private final JwtService jwtService;

    // 1. Перегляд постів користувача (неавторизований доступ)
    @GetMapping("/{email}")
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable String email) {
        List<PostDto> posts = postService.getPostsByUserEmail(email);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    // 2. Створення посту для користувача (може тільки власник)
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody PostRequest postRequest,
            @RequestHeader("Authorization") String token) {
        String userEmail = jwtService.extractUsername(token.substring(7)); // Видаляємо "Bearer "
        if (!userEmail.equals(postRequest.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Post post = postService.createPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {  // Отримуємо токен з заголовка
        String userEmail = jwtService.extractUsername(token.substring(7)); // Видаляємо "Bearer "
        postService.deletePost(postId, userEmail); // Використовуємо email з токена
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token,
            @RequestBody String newText) {
        String email = jwtService.extractUsername(token); // Витягнути email з токена
        PostDto updatedPost = postService.updatePost(postId, email, newText);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }
}
