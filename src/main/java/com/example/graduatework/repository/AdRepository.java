package com.example.graduatework.repository;

import com.example.graduatework.entity.Ad;
import com.example.graduatework.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
    List<Ad> findAllByAuthor(User author);
}
