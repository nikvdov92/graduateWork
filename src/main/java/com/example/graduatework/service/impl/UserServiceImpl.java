package com.example.graduatework.service.impl;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.UpdateUser;
import com.example.graduatework.dto.UserDto;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.UserMapper;
import com.example.graduatework.repository.UserRepository;
import com.example.graduatework.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public boolean setPassword(NewPassword newPassword, Authentication authentication) {
        try {
            User user = userRepository.findUserByEmail(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);
            if (user.getPassword().equals(newPassword.getCurrentPassword())) {
                user.setPassword(newPassword.getNewPassword());
                userRepository.save(user);
                log.info("Пароль изменён");
                return true;
            }
        } catch (Exception e) {
            log.warn("Пароль не изменён " + e);
            return false;
        }
        return false;
    }

    @Override
    public UserDto getUser(Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        UserDto userDto = userMapper.userToUserDto(user);
        log.info("Запрошенная информация: " + userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UpdateUser updateUser, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        user.setFirstName(updateUser.getFirstname());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        userRepository.save(user);
        UserDto userDto = userMapper.userToUserDto(user);
        log.info("Пользователь обновлён :" + userDto);
        return userDto;
    }

    @Override
    public void updateUserImage(MultipartFile image, Authentication authentication) {
        User user = userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        user.setImage(image.getName());
        userRepository.save(user);
        log.info("Изображение пользователя обновлено");
    }
}
