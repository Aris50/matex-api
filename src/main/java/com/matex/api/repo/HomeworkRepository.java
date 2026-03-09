package com.matex.api.repo;

import com.matex.api.domain.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    @Query(value = "SELECT * FROM homeworks WHERE created_at >= NOW() - INTERVAL 30 MINUTE ORDER BY created_at DESC", nativeQuery = true)
    List<Homework> findRecentHomeworks();

    @Query("SELECT h FROM Homework h ORDER BY h.createdAt DESC")
    List<Homework> findAllOrderByCreatedAtDesc();
}