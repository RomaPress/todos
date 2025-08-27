package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.TodoService;
import com.roman.pres.todos.service.command.Command;
import com.roman.pres.todos.service.command.CommandKey;
import com.roman.pres.todos.util.TodoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTodoCmd implements Command<TodoResponse> {
    private static final String TODO_NOT_FOUND_MEG_TEMPLATE ="Todo with id = %s not found";

    private final TodoService todoService;
    private final TodoRepository todoRepository;
    private final TodoUtil todoUtil;

    @Override
    public CommandKey getName() {
        return CommandKey.DELETE_TODO;
    }

    @Override
    public TodoResponse execute(RequestConfig config) {
        final Long todoId = config.getLong(RequestKey.TODO_ID);
        if (!todoService.isTodoExist(todoId)){
            return todoUtil.createErrorResponse(String.format(TODO_NOT_FOUND_MEG_TEMPLATE, todoId),
                    HttpStatus.NOT_FOUND.value());
        }
        try {
            return new TodoResponse(todoService.cleanupTodoWithTask(todoId));
        } catch (TodoPersistenceException e) {
            return todoUtil.createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
