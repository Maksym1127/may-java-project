package com.example.project.controller;

import com.example.project.dao.UserDAO;
import com.example.project.dto.UserDto;
import com.example.project.entity.User;
import com.example.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // Список всіх користувачів, та фільтрація по їх полях
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "email", required = false) String email) {

        List<UserDto> users;
        if (firstName != null && lastName != null) {
            users = userService.findAllUsersByFullName(firstName, lastName);
        } else if (firstName != null) {
            users = userService.findAllUsersByFirstName(firstName);
        } else if (lastName != null) {
            users = userService.findAllUsersByLastName(lastName);
        } else if (email != null) {
            users = userService.findAllUsersByEmail(email);
        } else {
            users = userService.findAllUsers();
        }
        if (users == null || users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("It seems like there is no user matching the given criteria");
        }
        return ResponseEntity.ok(users);
    }
    // Пошук користувача за ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.of(userService.findById(id));
    }

    // Пошук користувача за email /api/users/search?email=test@gmail.com
    @GetMapping("/users/search")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        Optional<UserDto> userDto = userService.findByEmail(email);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Видалення по email, /users/delete?email=example@gmail.com
    @DeleteMapping("/users/delete")
    public ResponseEntity<String> deleteUser(
            @RequestHeader("Authorization")
            String authorizationHeader,
            @RequestParam String email) {
        String token = authorizationHeader.replace("Bearer ", "");
        userService.deleteUserByEmail(email, token);
        return ResponseEntity.ok("Your account has been successfully deleted.");
    }

    //Оновлення користувача по вашому email
    @PutMapping("/users/update/{email}")
    public ResponseEntity<User> updateUser(
            @PathVariable String email,
            @RequestBody UserDto userDto,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        User updated = userService.updateUser(email, userDto, jwtToken);
        return ResponseEntity.ok(updated);
    }

}
