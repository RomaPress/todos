package com.roman.pres.todos.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roman.pres.todos.model.dao.TaskCollection;
import com.roman.pres.todos.model.dao.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"errorMessage", "errorCode"})
public class TodoResponse extends ApiResponse {
    private Long id;
    private String name;
    private String description;
    private List<TaskResponse> tasks = new ArrayList<>();

    public TodoResponse(Todo todoDao) {
        this.id = todoDao.getId();
        this.name = todoDao.getName();
        this.description = todoDao.getDescription();
        this.tasks.addAll(todoDao.getTaskCollections().stream()
                .map(TaskCollection::getTodoTask)
                .map(TaskResponse::new)
                .toList());
    }
}


