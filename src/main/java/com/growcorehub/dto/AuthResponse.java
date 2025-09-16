package com.growcorehub.dto;

import com.growcorehub.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	private Long userId;
	private String name;
	private String email;
	private User.Role role;
	private String message;
}
