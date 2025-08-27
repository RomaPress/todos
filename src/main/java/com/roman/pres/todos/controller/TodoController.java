package com.roman.pres.todos.controller;

import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dto.ApiResponse;
import com.roman.pres.todos.model.dto.TodoRequest;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.service.command.CommandExecutor;
import com.roman.pres.todos.service.command.CommandKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController extends BaseController{
    private final CommandExecutor commandExecutor;

    @GetMapping
    public List<TodoResponse> obtainAllTodos() {
        return commandExecutor.execute(CommandKey.GET_ALL_TODO, RequestConfig.EMPTY);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtainTodo(@PathVariable Long id) {
        TodoResponse response = commandExecutor.execute(CommandKey.GET_TODO_BY_ID,
                new RequestConfig(Pair.of(RequestKey.TODO_ID, id)));
        if (isFailedResponse(response)){
            return generateError(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createTodo(@RequestBody TodoRequest todoRequest) {
        TodoResponse response = commandExecutor.execute(CommandKey.CREATE_TODO,
                new RequestConfig(Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest)));
        if (isFailedResponse(response)){
            return generateError(response);
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTodo(@PathVariable Long id) {
        TodoResponse response = commandExecutor.execute(CommandKey.DELETE_TODO,
                new RequestConfig(Pair.of(RequestKey.TODO_ID, id)));
        if (isFailedResponse(response)){
            return generateError(response);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> overwriteTodo(@PathVariable Long id, @RequestBody TodoRequest todoRequest) {
        TodoResponse response = commandExecutor.execute(CommandKey.UPDATE_TODO_WITH_TASK, new RequestConfig(
                Pair.of(RequestKey.TODO_ID, id), Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest)));
        if (isFailedResponse(response)){
            return generateError(response);
        }
        return ResponseEntity.ok(response);
    }
}
