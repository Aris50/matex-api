package com.matex.api.controller.student;

import com.matex.api.service.SubmissionService;
import com.matex.api.web.dto.SubmissionCreatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/assignments/{assignmentId}/exercises/{exerciseId}/submissions")
public class StudentSubmissionController {

    private final SubmissionService submissionService;

    public StudentSubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionCreatedResponse submit(@RequestParam Long studentId,
                                            @PathVariable Long assignmentId,
                                            @PathVariable Long exerciseId,
                                            @RequestPart("files") List<MultipartFile> files,
                                            @RequestPart(value = "textResult", required = false) String textResult) {
        return submissionService.submit(studentId, assignmentId, exerciseId, files, textResult);
    }
}