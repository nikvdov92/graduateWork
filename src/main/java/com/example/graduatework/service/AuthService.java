package com.example.graduatework.service;

import com.example.graduatework.dto.Register;
import org.springframework.security.core.Authentication;

public interface AuthService {

    boolean login(String userName, String password);

    boolean register(Register register);
}
