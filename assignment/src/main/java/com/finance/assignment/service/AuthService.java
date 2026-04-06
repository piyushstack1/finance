package com.finance.assignment.service;

import com.finance.assignment.dto.*;
import com.finance.assignment.entity.Role;
import com.finance.assignment.entity.Status;
import com.finance.assignment.entity.User;
import com.finance.assignment.exception.CustomException;
import com.finance.assignment.repository.UserRepository;
import com.finance.assignment.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO signup(SignupRequestDTO request) {
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateToken(savedUser.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .email(savedUser.getEmail())
                .message("User registered successfully")
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomException("User not found"));

            if (user.getStatus() == Status.INACTIVE) {
                throw new CustomException("User account is inactive");
            }

            String token = tokenProvider.generateToken(request.getEmail());

            return AuthResponseDTO.builder()
                    .token(token)
                    .email(user.getEmail())
                    .message("Login successful")
                    .build();
        } catch (Exception e) {
            throw new CustomException("Invalid email or password");
        }
    }
}

