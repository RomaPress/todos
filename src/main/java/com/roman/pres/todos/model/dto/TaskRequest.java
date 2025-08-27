package com.roman.pres.todos.model.dto;

import com.roman.pres.todos.model.dao.TodoTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String name;
    private String description;

    public TodoTask convertToDao() {
        TodoTask todoTask = new TodoTask();
        todoTask.setName(name);
        todoTask.setDescription(description);
        return todoTask;
    }
}
