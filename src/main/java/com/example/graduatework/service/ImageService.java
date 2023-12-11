package com.example.graduatework.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ImageService {

    String uploadImage(String imageId, MultipartFile imageFile) throws IOException;

    void deleteImage(String image) throws IOException;

    byte[] getImage(String id) throws IOException;
}
