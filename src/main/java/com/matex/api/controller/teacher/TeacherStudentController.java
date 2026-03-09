package com.matex.api.controller.teacher;

import com.matex.api.domain.User;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher/students")
public class TeacherStudentController {

    private final UserRepository userRepository;

    public TeacherStudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAllStudents() {
        return userRepository.findByRole(UserRole.STUDENT)
                .stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "fullName", u.getFullName()
                ))
                .toList();
    }
}

