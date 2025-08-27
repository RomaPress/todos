package com.roman.pres.todos.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roman.pres.todos.model.dao.TodoTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"errorMessage", "errorCode"})
public class TaskResponse extends ApiResponse {
    private String name;
    private String description;

    public TaskResponse(TodoTask todoTaskDao) {
        this.name = todoTaskDao.getName();
        this.description = todoTaskDao.getDescription();
    }
}
