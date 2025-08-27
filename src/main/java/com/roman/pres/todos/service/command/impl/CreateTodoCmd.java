package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dao.TodoTask;
import com.roman.pres.todos.model.dto.TaskRequest;
import com.roman.pres.todos.model.dto.TodoRequest;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.service.TodoService;
import com.roman.pres.todos.service.command.Command;
import com.roman.pres.todos.service.command.CommandKey;
import com.roman.pres.todos.util.TodoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateTodoCmd implements Command<TodoResponse> {
    private final TodoService todoService;
    private final TodoUtil todoUtil;

    @Override
    public CommandKey getName() {
        return CommandKey.CREATE_TODO;
    }

    @Override
    public TodoResponse execute(RequestConfig config) {
        final TodoRequest todoRequest = config.getObject(RequestKey.TODO_REQUEST_OBJ, TodoRequest.class);
        var name = todoRequest.getName();
        var desc = todoRequest.getDescription();
        var taskAmount = todoRequest.getTasks().size();

        Todo todoDao = new Todo();
        todoDao.setName(name);
        todoDao.setDescription(todoUtil.getDescription(desc, name, taskAmount));

        List<TodoTask> todoTaskDaoList = todoRequest.getTasks().stream()
                .map(TaskRequest::convertToDao)
                .toList();
        try {
            return new TodoResponse(todoService.saveTodoWithTask(todoDao, todoTaskDaoList));
        } catch (TodoPersistenceException e) {
            return todoUtil.createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
