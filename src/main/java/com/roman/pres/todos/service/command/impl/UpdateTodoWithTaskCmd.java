package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dao.TodoTask;
import com.roman.pres.todos.model.dto.TaskRequest;
import com.roman.pres.todos.model.dto.TodoRequest;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.TodoService;
import com.roman.pres.todos.service.command.Command;
import com.roman.pres.todos.service.command.CommandKey;
import com.roman.pres.todos.util.TodoUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UpdateTodoWithTaskCmd implements Command<TodoResponse> {
    private static final String TODO_NOT_FOUND_MEG_TEMPLATE = "Todo with id = %s not found";

    private final TodoService todoService;
    private final TodoUtil todoUtil;

    private final TodoRepository todoRepository;


    @Override
    public CommandKey getName() {
        return CommandKey.UPDATE_TODO_WITH_TASK;
    }

    @Override
    public TodoResponse execute(RequestConfig config) {
        final Long todoId = config.getLong(RequestKey.TODO_ID);
        final TodoRequest todoRequest = config.getObject(RequestKey.TODO_REQUEST_OBJ, TodoRequest.class);
        if (!todoService.isTodoExist(todoId)) {
            return todoUtil.createErrorResponse(String.format(TODO_NOT_FOUND_MEG_TEMPLATE, todoId),
                    HttpStatus.NOT_FOUND.value());
        }
        var name = todoRequest.getName();
        var desc = todoRequest.getDescription();
        var taskAmount = todoRequest.getTasks().size();

        Todo todo = todoRepository.findById(todoId).get();
        todo.setName(name);
        todo.setDescription(todoUtil.getDescription(desc, name, taskAmount));

        List<TodoTask> newTask = todoRequest.getTasks().stream()
                .map(TaskRequest::convertToDao)
                .toList();
        try {
            return new TodoResponse(todoService.overwriteTodoWithTask(todo, newTask));
        } catch (TodoPersistenceException e) {
            return todoUtil.createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
