package com.example.graduatework.impl;

import com.example.graduatework.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CommentServiceImpl {

    private final CommentRepository commentRepository;
}
