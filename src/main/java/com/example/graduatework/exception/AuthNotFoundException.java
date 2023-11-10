package com.example.graduatework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AuthNotFoundException
 * Данное исключение выбрасывается когда в БД не найден пользователь с определёнными ролями
 * Исключение наследуется от {@link RuntimeException}
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthNotFoundException extends RuntimeException {

    public AuthNotFoundException() {
        super("Пользователь с такими полномочиями не найден");
    }
}
