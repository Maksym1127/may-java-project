package com.example.project.entity;

import com.example.project.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;  // Текст поста

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // Користувач, який створив пост

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // Дата створення поста

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();  // Дата останнього оновлення
}
