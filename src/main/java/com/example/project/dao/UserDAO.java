package com.example.project.dao;

import com.example.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findByToken(String token);

}
