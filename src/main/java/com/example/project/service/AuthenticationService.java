package com.example.project.service;

import com.example.project.dao.UserDAO;
import com.example.project.entity.*;
import com.example.project.entity.requests.AuthenticationRequest;
import com.example.project.entity.requests.AuthenticationResponse;
import com.example.project.entity.requests.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {

        if (userDAO.findUserByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists.");
        }
        User user = User
                .builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.AUTHORIZED)
                .build();
        userDAO.save(user);
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        User user = userDAO.findUserByEmail(authenticationRequest.getEmail())
                .orElseThrow();
        String token = jwtService.generateToken(user);
        user.setToken(token);
        user.setLastLogin(LocalDateTime.now());
        userDAO.save(user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public void logout (String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String jwt = token.substring(7);
        User user = userDAO.findByToken(jwt)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLogout(LocalDateTime.now());
        user.setToken(null);
        userDAO.save(user);
        AuthenticationResponse
                .builder().build();
    }

}
