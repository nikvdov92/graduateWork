package com.example.graduatework.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadImage(String imageId, MultipartFile imageFile) throws IOException;

    void deleteImage(String image);

}
