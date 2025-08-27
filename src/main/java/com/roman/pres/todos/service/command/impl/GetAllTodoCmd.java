package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.command.Command;
import com.roman.pres.todos.service.command.CommandKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllTodoCmd implements Command<List<TodoResponse>> {
    private final TodoRepository todoRepository;

    @Override
    public CommandKey getName() {
        return CommandKey.GET_ALL_TODO;
    }

    public List<TodoResponse> execute(RequestConfig config) {
        List<Todo> todoDaoList = todoRepository.findAll();
        return transformTodoDaoToTodoDto(todoDaoList);
    }

    private List<TodoResponse> transformTodoDaoToTodoDto(List<Todo> todoDaoList) {
        return todoDaoList.stream()
                .map(TodoResponse::new)
                .toList();
    }
}
