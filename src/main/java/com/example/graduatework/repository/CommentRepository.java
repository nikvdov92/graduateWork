package com.example.graduatework.repository;

import com.example.graduatework.entity.Ad;
import com.example.graduatework.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByAdId(int id);

    void deleteAllByAdId(int adId);
}
