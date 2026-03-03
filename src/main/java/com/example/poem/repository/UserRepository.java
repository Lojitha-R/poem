package com.example.poem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.poem.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}