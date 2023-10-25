package com.example.graduatework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UserNotFoundException
 * Данное исключение выбрасывается когда в базе данных (БД) не найден пользователь
 * Наследуется от {@link RuntimeException}
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Пользователь не найден");
    }
}
