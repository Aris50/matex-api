package com.matex.api.service;

import com.matex.api.domain.Exercise;
import com.matex.api.domain.Homework;
import com.matex.api.repo.ExerciseRepository;
import com.matex.api.repo.HomeworkRepository;
import com.matex.api.web.dto.CreateExerciseRequest;
import com.matex.api.mapper.ExerciseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.IOException;

@Service
public class ExerciseService {

    private final HomeworkRepository homeworkRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper = new ExerciseMapper();
    private final ExerciseImageStorageService exerciseImageStorageService;

    public ExerciseService(HomeworkRepository homeworkRepository, ExerciseRepository exerciseRepository, ExerciseImageStorageService exerciseImageStorageService) {
        this.homeworkRepository = homeworkRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseImageStorageService = exerciseImageStorageService;
    }

    public Exercise addExercise(Long homeworkId, String instructionText, MultipartFile image) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework not found"));

        int nextOrder = exerciseRepository.findMaxOrderIndexByHomeworkId(homeworkId) + 1;

        Exercise exercise = new Exercise();
        exercise.setHomework(homework);
        exercise.setOrderIndex(nextOrder);
        exercise.setInstructionText(instructionText);
        Exercise saved = exerciseRepository.save(exercise);

        if (image != null && !image.isEmpty()) {
            try {
                ExerciseImageStorageService.StoredExerciseImage stored = exerciseImageStorageService.store(saved.getId(), image);

                saved.setImagePath(stored.imagePath());
                saved.setImageOriginalName(stored.imageOriginalName());
                saved.setImageContentType(stored.imageContentType());
                saved.setImageSizeBytes(stored.imageSizeBytes());

                saved = exerciseRepository.save(saved);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store exercise image");
            }
        }

        return saved;
    }

    public List<Exercise> getExercisesForHomework(Long homeworkId) {
        return exerciseRepository.findByHomeworkId(homeworkId);
    }
}