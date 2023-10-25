package com.example.graduatework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AdNotFoundException
 * Данное исключение выбрасывается когда в базе данных (БД) не найдено объявление
 * Наследуется от {@link RuntimeException}
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdNotFoundException extends RuntimeException {

    public AdNotFoundException() {
        super("Объявление не найдено");
    }
}
