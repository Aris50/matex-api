package com.matex.api.mapper;

import com.matex.api.domain.Homework;
import com.matex.api.domain.User;
import com.matex.api.web.dto.CreateHomeworkRequest;
import com.matex.api.web.dto.HomeworkResponse;

public class HomeworkMapper {

    public Homework toEntity(CreateHomeworkRequest req, User teacher) {
        Homework hw = new Homework();
        hw.setTeacher(teacher);
        hw.setTitle(req.title());
        hw.setDescription(req.description());
        hw.setDueAt(req.dueAt());
        return hw;
    }

    public HomeworkResponse toResponse(Homework hw) {
        return new HomeworkResponse(
                hw.getId(),
                hw.getTeacher().getId(),
                hw.getTitle(),
                hw.getDescription(),
                hw.getDueAt(),
                hw.getCreatedAt()
        );
    }
}