package com.example.graduatework.service.impl;

import com.example.graduatework.dto.Register;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.repository.UserRepository;
import com.example.graduatework.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    /**
     * Вход пользователя
     */

    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    /**
     * Регистрация пользователя
     */

    @Override
    public boolean register(Register register) {
        if (manager.userExists(register.getUsername())) {
            return false;
        }
        manager.createUser(
                User.builder()
                        .passwordEncoder(this.encoder::encode)
                        .password(register.getPassword())
                        .username(register.getUsername())
                        .roles(register.getRole().name())
                        .build());

        com.example.graduatework.entity.User user = userRepository.findUserByEmail(register.getUsername())
                .orElseThrow(UserNotFoundException::new);

        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPhone(register.getPhone());
        user.setRegDate(LocalDateTime.now());
        userRepository.save(user);
        log.info("Новый пользователь создан");
        return true;
    }
}
