package com.growcorehub.repository;

import com.growcorehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.role = :role")
	java.util.List<User> findByRole(@Param("role") User.Role role);

	@Query("SELECT u FROM User u WHERE u.status = :status")
	java.util.List<User> findByStatus(@Param("status") User.UserStatus status);
}