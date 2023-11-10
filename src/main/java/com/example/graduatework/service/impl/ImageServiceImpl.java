package com.example.graduatework.service.impl;

import com.example.graduatework.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.String;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    @Value("${image.dir.path}")
    private String imageDir;


    /**
     * Загрузка изображения
     */

    @Override
    public String uploadImage(String imageId, MultipartFile imageFile) throws IOException {
        String imageName = imageId + "." + getExtensions(Objects.requireNonNull(imageFile.getOriginalFilename()));

        Path filePath = Path.of(String.format("%s/%s", imageDir, imageName));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        Files.write(filePath, imageFile.getInputStream().readAllBytes());
        return imageName.replace(".", "");
    }

    /**
     * Удаление изображения
     */
    @Override
    public void deleteImage(String image) {
        File imageFile = new File(imageDir + image);
        imageFile.delete();
    }

    /**
     * Изменить изображение
     */

    @Override
    public void getImage(String imageName, HttpServletResponse response) throws IOException {
        Path filePath = Path.of(String.format("%s/%s", imageDir, imageName.replace("_", ".")));
        try (InputStream is = Files.newInputStream(filePath);
             OutputStream os = response.getOutputStream()) {
            response.setStatus(200);
            is.transferTo(os);
        }
    }

    /**
     * Получение расширения файла
     */

    private String getExtensions(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null || extension.length() == 0) {
            throw new IllegalArgumentException("Неверное имя файла: " + filename);
        }
        return extension;
    }
}
