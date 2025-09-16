package com.growcorehub.service;

import com.growcorehub.dto.AuthRequest;
import com.growcorehub.dto.AuthResponse;
import com.growcorehub.dto.SignupRequest;
import com.growcorehub.entity.User;
import com.growcorehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

	private final UserRepository userRepository;

	public AuthResponse signup(SignupRequest request) {
		// Check if user already exists
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		// Create new user (without password hashing for simplicity)
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPasswordHash(request.getPassword()); // In real app, hash this
		user.setRole(User.Role.FREELANCER);
		user.setStatus(User.UserStatus.ACTIVE); // Simplified - no email verification

		user = userRepository.save(user);

		log.info("User registered successfully: {}", user.getEmail());

		return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(),
				"Registration successful");
	}

	public AuthResponse login(AuthRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		// Simple password check (in real app, use password encoder)
		if (!user.getPasswordHash().equals(request.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}

		if (user.getStatus() != User.UserStatus.ACTIVE) {
			throw new RuntimeException("Account is not active");
		}

		log.info("User logged in successfully: {}", user.getEmail());

		return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), "Login successful");
	}
}