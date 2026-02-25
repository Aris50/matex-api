package com.matex.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "exercises",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_exercise_order", columnNames = {"homework_id", "order_index"})
        }
)
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "instruction_text", nullable = false, columnDefinition = "TEXT")
    private String instructionText;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
}