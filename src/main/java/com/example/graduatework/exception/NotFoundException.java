package com.example.graduatework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Не найден");
    }
}
