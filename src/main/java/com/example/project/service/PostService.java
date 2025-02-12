package com.example.project.service;
import com.example.project.entity.requests.PostRequest;

import com.example.project.dao.PostRepository;
import com.example.project.dao.UserDAO;
import com.example.project.dto.PostDto;
import com.example.project.entity.Post;
import com.example.project.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserDAO userDAO;
    private final JwtService jwtService;
    // Перегляд постів користувача (неавторизований доступ)
    public List<PostDto> getPostsByUserEmail(String email) {
        List<Post> posts = postRepository.findAllByUserEmail(email);
        return posts.stream()
                .map(post -> PostDto.builder()
                        .id(post.getId())
                        .text(post.getText())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build())
                .toList();
    }

    // Створення посту для користувача
    public Post createPost(PostRequest postRequest) {
        User user = userDAO.findUserByEmail(postRequest.getUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = Post.builder()
                .text(postRequest.getText())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }

    // Видалення посту користувача
    public void deletePost(Long postId, String email) {
        Post post = postRepository.findByIdAndUserEmail(postId, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts"));

        postRepository.delete(post);
    }

    // Редагування посту користувача
    public PostDto updatePost(Long postId, String email, String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text cannot be empty");
        }

        Post post = postRepository.findByIdAndUserEmail(postId, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own posts"));

        post.setText(newText);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return PostDto.builder()
                .id(post.getId())
                .text(post.getText())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
