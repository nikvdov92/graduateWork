package com.example.graduatework.controller;

import com.example.graduatework.dto.NewPassword;
import com.example.graduatework.dto.UpdateUser;
import com.example.graduatework.dto.User;
import com.example.graduatework.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping(value = "http://localhost:3000/users")
@Tag(name = "Управление пользователями", description = "CRUD-операции для работы с пользователями")
@RequiredArgsConstructor
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Все ок."),
        @ApiResponse(responseCode = "400", description = "Ошибка в параметрах запроса."),
        @ApiResponse(responseCode = "404", description = "Неверный URL-адрес"),
        @ApiResponse(responseCode = "500", description = "Ошибка на сервере.")
})


public class UserController {
    private final UserService userService;

    @GetMapping ("/set_password")
    @Operation(summary = "Обновление пароля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<String> setPassword(@RequestBody NewPassword newPassword) {
        if (!verifyCurrentPassword(newPassword.getCurrentPassword())) {
            return new ResponseEntity<>("Текущий пароль неверный", HttpStatus.UNAUTHORIZED);
        }

        if (isWeakPassword(newPassword.getNewPassword())) {
            return new ResponseEntity<>("Слишком легкий пароль", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("Пароль успешно обновлен", HttpStatus.OK);
    }
    private boolean verifyCurrentPassword(String currentPassword) {
        return true;
    }

    private boolean isWeakPassword(String newPassword) {
        return false;
    }


    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<User> getUser(@RequestBody User user) {
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping
    @Operation(summary = "Обновление информации об авторизованном пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser) {
        if (updateUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updateUser);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/me/image")
    @Operation(summary = "Обновление аватара авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> updateUserImage(@RequestParam("image") MultipartFile image) {
        if (!image.isEmpty()) {
            User user = userService.getAuthenticatedUser();
            if (user != null) {
                try {
                    String fileName = saveImageToStorage(image);
                    user.setImage(fileName);
                    userService.updateUser(user);
                    return ResponseEntity.status(HttpStatus.OK).body("Аватар успешно обновлен");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось сохранить изображение");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не авторизован");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Файл не выбран");
        }
    }
    private String saveImageToStorage(MultipartFile image) {
        String fileName = image.getOriginalFilename();
        return fileName;
    }

}
