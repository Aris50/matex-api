package com.matex.api.controller.teacher;

import com.matex.api.domain.StudentAssignment;
import com.matex.api.service.AssignmentService;
import com.matex.api.web.dto.AssignmentResponse;
import com.matex.api.web.dto.CreateAssignmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher/homeworks/{homeworkId}/assignments")
public class TeacherAssignmentController {

    private final AssignmentService assignmentService;

    public TeacherAssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<AssignmentResponse> assign(@PathVariable Long homeworkId, @RequestBody CreateAssignmentRequest req) {
        List<StudentAssignment> created = assignmentService.assignHomework(homeworkId, req);
        return created.stream()
                .map(sa -> new AssignmentResponse(
                        sa.getId(),
                        sa.getHomework().getId(),
                        sa.getStudent().getId(),
                        sa.getStatus().name(),
                        sa.getAssignedAt()
                ))
                .toList();
    }
}