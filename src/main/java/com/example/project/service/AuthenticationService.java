package com.example.project.service;

import com.example.project.dao.UserDAO;
import com.example.project.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {

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
        userDAO.save(user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse logout (String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }

        String jwt = token.substring(7);

        User user = userDAO.findByToken(jwt)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setToken(null);
        userDAO.save(user);
        return AuthenticationResponse
                .builder().build();
    }

//    public AuthenticationResponse logout(String token) {
//
//
//    }
}
