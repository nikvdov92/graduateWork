package com.example.graduatework.service;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.UpdateUser;
import com.example.graduatework.dto.UserDto;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    boolean setPassword(NewPassword newPassword, Authentication authentication);

    UserDto getUser(Authentication authentication);

    UserDto updateUser(UpdateUser updateUser, Authentication authentication);

    void updateUserImage(MultipartFile image, Authentication authentication);


}
