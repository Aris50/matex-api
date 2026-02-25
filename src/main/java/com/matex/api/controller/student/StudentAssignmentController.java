package com.matex.api.controller.student;

import com.matex.api.service.StudentAssignmentQueryService;
import com.matex.api.web.dto.StudentAssignmentListItemResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/assignments")
public class StudentAssignmentController {

    private final StudentAssignmentQueryService queryService;

    public StudentAssignmentController(StudentAssignmentQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public List<StudentAssignmentListItemResponse> list(@RequestParam Long studentId) {
        return queryService.listForStudent(studentId);
    }
}