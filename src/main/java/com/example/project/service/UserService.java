package com.example.project.service;

import com.example.project.dao.UserDAO;
import com.example.project.dto.UserDto;
import com.example.project.entity.User;
import com.example.project.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserDAO userDAO;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public void deleteUserByEmail(String email, String token) {
        String authenticatedEmail = jwtService.extractUsername(token); // Отримуємо email з токена
        if (!authenticatedEmail.equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account.");
        }
        User user = userDAO.findUserByEmail(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        userDAO.delete(user); // Видаляємо користувача
    }

    public User updateUser(String email, UserDto userDto, String token) {
        // Отримуємо email аутентифікованого користувача з токена
        String authenticatedEmail = jwtService.extractUsername(token);
        if (!authenticatedEmail.equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own account.");
        }
        User user = userDAO.findUserByEmail(email).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        // Зберігаємо оновленого користувача
        return userDAO.save(user);
    }

    public List<UserDto> findAllUsers() {
        return userDAO
                .findAll()
                .stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    public Optional<UserDto> findById(Long id) {
        return userDAO
                .findById(id)
                .map(userMapper::mapToDto);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userDAO
                .findUserByEmail(email)
                .map(userMapper::mapToDto);
    }

    public List<UserDto> findAllUsersByFirstName(String firstName) {
        return userDAO.findByFirstName(firstName).stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> findAllUsersByLastName(String lastName) {
        return userDAO.findByLastName(lastName).stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> findAllUsersByEmail(String email) {
        return userDAO.findByEmail(email).stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> findAllUsersByFullName(String firstName, String lastName) {
        return userDAO.findByFirstNameContainingAndLastName(firstName, lastName).stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }


}
