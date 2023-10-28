package com.example.graduatework.service.impl;

import com.example.graduatework.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    @Value("${image.dir.path}")
    private final String imageDir;

    /**
     * Загрузка изображения
     */

    @Override
    public String uploadImage(String imageId, MultipartFile imageFile) throws IOException {
        String filename = imageId + "." + getFileExtension(imageFile.getOriginalFilename());
        Path imagePath = Path.of(imageDir, filename);

        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return imagePath.toString();
    }

    @Override
    public void deleteImage(String image) {
        File imageFile = new File(imageDir + image);
        imageFile.delete();
    }

        /**
         * Получение расширения файла
         */

        private String getFileExtension (String filename){
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex == -1 || dotIndex == filename.length() - 1) {
                throw new IllegalArgumentException("Неверное имя файла: " + filename);
            }
            return filename.substring(dotIndex + 1);
        }
    }
