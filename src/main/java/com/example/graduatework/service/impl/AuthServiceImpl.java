package com.example.graduatework.service.impl;

import com.example.graduatework.dto.Register;
import com.example.graduatework.entity.User;
import com.example.graduatework.mapper.UserMapper;
import com.example.graduatework.service.AuthService;
import com.example.graduatework.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    /**
     * Вход пользователя
     */

    @Override
    public boolean login(String userName, String password) {
        com.example.graduatework.entity.User user = userService.getUser(userName);
        if (user == null) {
            return false;
        }
        return encoder.matches(password, user.getPassword());
    }

    /**
     * Регистрация пользователя
     */

    @Override
    public boolean register(Register register) {
        if (userService.getUser(register.getUsername()) != null) {
            return false;
        }
        User user = userMapper.registerToUser(register);
        user.setPassword(encoder.encode(user.getPassword()));
        userService.saveUser(user);
        log.info("Новый пользователь создан");
        return true;
    }
}
