package com.example.graduatework.service.impl;

import com.example.graduatework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl {
    private final UserRepository userRepository;
}
