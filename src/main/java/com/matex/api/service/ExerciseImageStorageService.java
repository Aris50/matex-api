package com.matex.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ExerciseImageStorageService {

    private final Path storageRoot;

    public ExerciseImageStorageService(@Value("${matex.storage.root}") String storageRoot) {
        this.storageRoot = Paths.get(storageRoot).toAbsolutePath().normalize();
    }

    public StoredExerciseImage store(Long exerciseId, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        Files.createDirectories(storageRoot);

        Path exerciseDir = storageRoot.resolve("exercises").resolve(exerciseId.toString());
        Files.createDirectories(exerciseDir);

        String originalName = image.getOriginalFilename() != null ? image.getOriginalFilename() : "image";
        String safeName = UUID.randomUUID() + "-" + Paths.get(originalName).getFileName();

        Path target = exerciseDir.resolve(safeName);

        try (InputStream in = image.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativePath = Paths.get("exercises", exerciseId.toString(), safeName).toString().replace("\\", "/");

        return new StoredExerciseImage(
                relativePath,
                originalName,
                image.getContentType(),
                image.getSize()
        );
    }

    public record StoredExerciseImage(
            String imagePath,
            String imageOriginalName,
            String imageContentType,
            Long imageSizeBytes
    ) {}
}