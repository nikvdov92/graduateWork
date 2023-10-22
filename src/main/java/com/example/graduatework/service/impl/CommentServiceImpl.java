package com.example.graduatework.service.impl;

import com.example.graduatework.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl {

    private final CommentRepository commentRepository;
}
