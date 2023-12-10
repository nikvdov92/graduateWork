package com.example.graduatework.service;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.UpdateUser;
import com.example.graduatework.dto.UserDto;
import com.example.graduatework.entity.User;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    boolean setPassword(NewPassword newPassword, Authentication authentication);

    UserDto getUserDto(Authentication authentication);

    User getUser(String email);

    UserDto updateUser(UpdateUser updateUser, Authentication authentication);

    void saveUser(User user);

    void updateUserImage(MultipartFile image, Authentication authentication);
}
