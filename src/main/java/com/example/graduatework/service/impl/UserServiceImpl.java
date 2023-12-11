package com.example.graduatework.service.impl;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.UpdateUser;
import com.example.graduatework.dto.UserDto;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.UserMapper;
import com.example.graduatework.repository.UserRepository;
import com.example.graduatework.service.ImageService;
import com.example.graduatework.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * Обновление пароля
     */
    @Transactional
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
    public UserDto getUserDto(Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        UserDto userDto = userMapper.userToUserDto(user);
        log.info("Информация запрошена: " + userDto);
        return userDto;
    }

    /**
     * Найти пользователя по электронной почте
     */
    @Override
    public User getUser(String email) {
        try {
            User user = userRepository.findUserByEmail(email)
                    .orElseThrow(UserNotFoundException::new);
            log.info("Found user: " + user);
            return user;
        } catch (UserNotFoundException e) {
            log.info("User not found: " + email);
            return null;
        }
    }

    /**
     * Обновить информацию о текущем пользователе
     */
    @Transactional
    @Override
    public UserDto updateUser(UpdateUser updateUser, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        userRepository.save(user);
        userRepository.flush();
        UserDto userDto = userMapper.userToUserDto(user);
        log.info("Информация пользователя обновлена: " + userDto);
        return userDto;
    }

    /**
     * Сохранить пользователя
     */
    @Override
    public void saveUser(User user) {
        userRepository.saveAndFlush(user);
        log.info("Пользователь сохранён: " + user);
    }

    /**
     * Обновление аватара авторизованного пользователя
     */

    @Override
    public void updateUserImage(MultipartFile imageFile, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        try {
            user.setImage(imageService.uploadImage("user/" + user.getId(), imageFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userRepository.saveAndFlush(user);
        log.info("Изображение пользователя обновлено");
    }
}
