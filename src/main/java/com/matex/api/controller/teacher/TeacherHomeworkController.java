package com.matex.api.controller.teacher;

import com.matex.api.domain.Homework;
import com.matex.api.service.HomeworkService;
import com.matex.api.web.dto.CreateHomeworkRequest;
import com.matex.api.web.dto.HomeworkResponse;
import com.matex.api.mapper.HomeworkMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher/homeworks")
public class TeacherHomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkMapper homeworkMapper = new HomeworkMapper();

    public TeacherHomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HomeworkResponse create(@RequestBody CreateHomeworkRequest req, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Homework hw = homeworkService.createHomework(req, userId);
        return homeworkMapper.toResponse(hw);
    }

    @GetMapping
    public List<HomeworkResponse> getAll(@RequestParam(required = false, defaultValue = "false") boolean recentOnly) {
        return homeworkService.getHomeworks(recentOnly)
                .stream()
                .map(homeworkMapper::toResponse)
                .toList();
    }
}