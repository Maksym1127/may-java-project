package com.example.project.dao;

import com.example.project.dto.UserDto;
import com.example.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findByToken(String token);

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    List<User> findByEmail(String email);

    List<User> findByFirstNameContainingAndLastName(String firstName, String lastName);


}
