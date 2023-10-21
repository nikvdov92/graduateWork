package com.example.graduatework.service;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.User;

public interface UserService {

    User getAuthenticatedUser();

    void updateUser(User user);

    void newPassword(String newPassword, String currentPassword);


    void setPassword(NewPassword newPassword);


}
