package com.matex.api.repo;

import com.matex.api.domain.SubmissionFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {
    List<SubmissionFile> findBySubmissionIdOrderByOrderIndexAsc(Long submissionId);
}