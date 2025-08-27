package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.command.Command;
import com.roman.pres.todos.service.command.CommandKey;
import com.roman.pres.todos.util.TodoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetTodoCmd implements Command<TodoResponse> {
    public static final String TODO_NOT_FOUND_MESSAGE_TEMPLATE = "Todo with id %s not found";
    private final TodoRepository todoRepository;
    private final TodoUtil todoUtil;

    @Override
    public CommandKey getName() {
        return CommandKey.GET_TODO_BY_ID;
    }

    @Override
    public TodoResponse execute(RequestConfig config) {
        final Long todoId = config.getLong(RequestKey.TODO_ID);
        Optional<Todo> todoDao = todoRepository.findById(todoId);
        return todoDao.map(TodoResponse::new)
                .orElse(todoUtil.createErrorResponse(String.format(TODO_NOT_FOUND_MESSAGE_TEMPLATE, todoId),
                        HttpStatus.NOT_FOUND.value()));
    }
}
