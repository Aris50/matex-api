package com.matex.api.service;

import com.matex.api.domain.Homework;
import com.matex.api.domain.User;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.HomeworkRepository;
import com.matex.api.repo.UserRepository;
import com.matex.api.web.dto.CreateHomeworkRequest;
import com.matex.api.mapper.HomeworkMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;
    private final HomeworkMapper homeworkMapper = new HomeworkMapper();

    public HomeworkService(HomeworkRepository homeworkRepository, UserRepository userRepository) {
        this.homeworkRepository = homeworkRepository;
        this.userRepository = userRepository;
    }

    public Homework createHomework(CreateHomeworkRequest req, Long userId) {
        if (req.title() == null || req.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }

        User teacher = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        if (teacher.getRole() != UserRole.TEACHER && teacher.getRole() != UserRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a teacher or owner");
        }

        Homework hw = homeworkMapper.toEntity(req, teacher);
        return homeworkRepository.save(hw);
    }

    public List<Homework> getHomeworks(boolean recentOnly) {
        if (recentOnly) {
            return homeworkRepository.findRecentHomeworks();
        }
        return homeworkRepository.findAllOrderByCreatedAtDesc();
    }
}
