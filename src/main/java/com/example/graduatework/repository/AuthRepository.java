package com.example.graduatework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.graduatework.entity.Auth;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Integer> {
    Optional<Auth> findByUsername(String username);
}
