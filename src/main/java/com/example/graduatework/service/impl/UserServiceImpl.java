package com.example.graduatework.service.impl;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.UpdateUser;
import com.example.graduatework.dto.UserDto;
import com.example.graduatework.entity.Auth;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.AuthNotFoundException;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.UserMapper;
import com.example.graduatework.repository.AuthRepository;
import com.example.graduatework.repository.UserRepository;
import com.example.graduatework.service.ImageService;
import com.example.graduatework.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final AuthRepository authoritiesRepository;

    /**
     * Обновление пароля
     */

    @Override
    public boolean setPassword(NewPassword newPassword, Authentication authentication) {
            User user = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            log.warn("Пароль не изменён");
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
        log.info("Пароль изменён");
        return true;
    }

    /**
     * Получение информации об авторизованном пользователе
     */

    @Override
    public UserDto getUser(Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        Auth auth = authoritiesRepository.findByUsername(user.getEmail())
                .orElseThrow(AuthNotFoundException::new);
        UserDto userDto = userMapper.userToUserDto(user, auth);
        log.info("Информация запрошена: " + userDto);
        return userDto;
    }

    /**
     * Обновление информации об авторизованном пользователе
     */

    @Override
    public UserDto updateUser(UpdateUser updateUser, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        user.setFirstName(updateUser.getFirstname());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        userRepository.save(user);
        Auth auth = authoritiesRepository.findByUsername(user.getEmail())
                .orElseThrow(AuthNotFoundException::new);
        UserDto userDto = userMapper.userToUserDto(user, auth);
        log.info("Пользователь обновлён :" + userDto);
        return userDto;
    }

    /**
     * Обновление аватара авторизованного пользователя
     */

    @Override
    public void updateUserImage(MultipartFile image, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        try {
            user.setImage(imageService.uploadImage("user" + user.getId(), image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userRepository.saveAndFlush(user);
        log.info("Изображение пользователя обновлено");
    }
}
