package com.example.graduatework.service;

import com.example.graduatework.dto.Register;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(Register register);
}
