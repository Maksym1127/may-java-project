package com.example.project.dao;

import com.example.project.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserEmail(String email);

    Optional<Post> findByIdAndUserEmail(Long id, String email);
}
