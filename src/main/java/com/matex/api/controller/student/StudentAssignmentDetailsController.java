package com.matex.api.controller.student;

import com.matex.api.service.StudentAssignmentDetailsService;
import com.matex.api.web.dto.StudentAssignmentDetailsResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student/assignments")
public class StudentAssignmentDetailsController {

    private final StudentAssignmentDetailsService detailsService;

    public StudentAssignmentDetailsController(StudentAssignmentDetailsService detailsService) {
        this.detailsService = detailsService;
    }

    @GetMapping("/{assignmentId}")
    public StudentAssignmentDetailsResponse get(@RequestParam Long studentId, @PathVariable Long assignmentId) {
        return detailsService.getDetails(studentId, assignmentId);
    }
}