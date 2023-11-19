package com.example.graduatework.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserNotFoundException
 * Данное исключение выбрасывается когда в базе данных (БД) не найден пользователь
 * Наследуется от {@link RuntimeException}
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
@ApiResponse(responseCode = "404", description = "Not Found")

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Пользователь не найден");
    }
}
