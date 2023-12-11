package com.example.graduatework.controller;

import com.example.graduatework.service.ImageService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/image")
@Tag(name = "Изображения")
@RequiredArgsConstructor

public class ImageController {
    private final ImageService imageService;

    @GetMapping(value = "/user/{id}")
    public byte[] getAvatar (@PathVariable String id) throws IOException {
        return imageService.getImage("user/" + id);
    }

    @GetMapping(value = "/ad/{id}")
    public byte[] getImage (@PathVariable String id) throws IOException {
        return imageService.getImage("ad/" + id);
    }
}
