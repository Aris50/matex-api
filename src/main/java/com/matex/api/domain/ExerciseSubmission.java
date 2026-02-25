package com.matex.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "exercise_submissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_attempt", columnNames = {"student_assignment_id", "exercise_id", "attempt_no"})
        }
)
public class ExerciseSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_assignment_id", nullable = false)
    private StudentAssignment studentAssignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo;

    @Column(name = "text_result", columnDefinition = "TEXT")
    private String textResult;

    @Column(name = "submitted_at", nullable = false, insertable = false, updatable = false)
    private Instant submittedAt;
}